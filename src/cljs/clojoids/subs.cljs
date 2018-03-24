(ns clojoids.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::spaceship
  :spaceship)

(re-frame/reg-sub
  ::time
  (fn [db]
    (int (quot (:tick db) (/ 1000 16)))))

(re-frame/reg-sub
  ::bullets
  :bullets)

(re-frame/reg-sub
  ::asteroids
  :asteroids)