(ns clojoids.events
  (:require [re-frame.core :as re-frame]
            [clojoids.db :as db]
            [clojoids.game :as game]
            [clojoids.config :as config]))


(defn handle-key [db key]
  (case key
    :up (-> db
            (assoc-in [:spaceship :engine?] true)
            (update :spaceship game/update-movement))
    :down db                                                ; Rocket brakes? LAME!
    :left (update-in db [:spaceship :direction] game/turn-left)
    :right (update-in db [:spaceship :direction] game/turn-right)
    :shoot (if (zero? (:bullet-cooldown db))
             (-> db
                 (assoc :bullet-cooldown (/ 1 config/firing-rate))
                 (update :bullets conj (game/shoot (:spaceship db))))
             db)))

(defn map-function-on-map-vals [m f]
  (reduce (fn [altered-map [k v]] (assoc altered-map k (f v))) {} m))

(defn handle-keys [{keys :keys :as db}]
  (reduce handle-key db keys))

(defn every [x tick]
  (zero? (mod tick x)))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  ::tick
  (fn [{tick :tick :as db} [_]]
    (when (every 5 tick) (game/dispatch-collisions! (:asteroids db) (:bullets db)))
    (cond-> db
            :always (assoc-in [:spaceship :engine?] false)
            :always handle-keys

            (pos? (:bullet-cooldown db)) (update :bullet-cooldown dec)

            (and (< (count (:asteroids db)) config/max-asteroids)
                 (zero? (mod tick config/asteroid-spawn-rate)))
            (update :asteroids conj (game/spawn-asteroid 3))

            :always (update :bullets game/update-bullets)
            :always (update :spaceship game/move-entity)
            :always (update :asteroids #(map game/move-entity %))
            :always (update :bullets #(map game/move-entity %))
            :always (update :tick inc))))

(re-frame/reg-event-db
  ::key-down
  (fn [db [_ key down?]]
    (update db :keys (if down? conj disj) key)))

(re-frame/reg-event-db
  ::bullet-collison
  (fn [db [_ {:keys [position size] :as asteroid} bullet]]
    (if (< 1 size)
      (-> db
          (update :asteroids conj (game/spawn-asteroid position (dec size)))
          (update :asteroids conj (game/spawn-asteroid position (dec size)))
          (update :bullets (partial filter #(not= (:id %) (:id bullet))))
          (update :asteroids (partial filter #(not= (:id %) (:id asteroid)))))
      (-> db
        (update :bullets (partial filter #(not= (:id %) (:id bullet))))
        (update :asteroids (partial filter #(not= (:id %) (:id asteroid))))))))