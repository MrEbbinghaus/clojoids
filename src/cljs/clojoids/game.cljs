(ns clojoids.game
  (:require [clojoids.config :as config]
            [re-frame.core :as re-frame]))

(defn ms->ticks [x]
  (/ x config/game-speed))

(defn- deg->rad [degree]
  (* degree (/ js/Math.PI 180)))

(defn distance [[x1 y1] [x2 y2]]
  (let [x (Math/abs (- x1 x2))
        y (Math/abs (- y1 y2))]
    (js/Math.sqrt (+ (* x x) (* y y)))))

(defn turn-left [direction]
  (mod (- direction config/turning-speed) 360))

(defn turn-right [direction]
  (mod (+ direction config/turning-speed) 360))

(defn update-movement [spaceship]
  (let [direction (deg->rad (:direction spaceship))
        rel-movement [(* config/spaceship-acceleration (Math/cos direction))
                      (* config/spaceship-acceleration (Math/sin direction))]]
    (update spaceship :movement #(map + % rel-movement))))

(defn wrap-around-world [pos]
  (map #(mod (+ %1 %2) %2) pos config/dimensions))


(defn move-entity [entity]
  (let [rel-pos (:movement entity)]
    (update entity :position #(wrap-around-world (map + % rel-pos)))))


(defn shoot [{:keys [position movement direction]}]
  (let [bullet-acceleration (* config/spaceship-acceleration 50)
        bullet-direction (deg->rad direction)
        rel-movement [(* bullet-acceleration (Math/cos bullet-direction))
                      (* bullet-acceleration (Math/sin bullet-direction))]]
    {:id       (random-uuid)
     :ttl      (ms->ticks config/bullet-ttl)
     :position position
     :movement (map + movement rel-movement)}))

(defn update-bullets [bullets]
  (->> bullets
       (map #(update % :ttl dec))
       (filter #(pos? (:ttl %)))))

(defn spawn-asteroid
  ([] (spawn-asteroid 3))
  ([size] (let [[dx dy] config/dimensions]
            (spawn-asteroid [(rand-int dx) (rand-int dy)] size)))
  ([position size]
   (let [speed 0.25
         rand-speed #(- (rand speed) (/ speed 2))]
     {:id (random-uuid)
      :position position
      :movement [(rand-speed) (rand-speed)]
      :size     size})))

(defn dispatch-collisions! [asteroids bullets]
  (doseq [asteroid asteroids
          bullet bullets
          :when (> (:size asteroid) (distance (:position asteroid) (:position bullet)))]
    (re-frame/dispatch [:clojoids.events/bullet-collison asteroid bullet])))

