(ns clojoids.views
  (:require [re-frame.core :as re-frame]
            [clojoids.subs :as subs]
            [clojoids.config :as config]))

(defn spaceship []
  (let [{:keys [position direction engine?]} @(re-frame/subscribe [::subs/spaceship])]
    [:g.spaceship
     {:transform (str "translate(" (first position) " " (second position) ")"
                      " rotate(" (+ 90 direction) ")")}
     [:polygon.engine {:points    "0 -1  -1 1  1 1"
                       :transform (str "translate(0, 0.71) rotate(180) scale(0.5 " (if engine? 0.6 0.2) ")")}
      [:animateTransform {:attributeName "transform"
                          :type          "scale"
                          :additive      "sum"
                          :repeatCount   "indefinite"
                          :from          "1 1"
                          :to            "1 1.4"
                          :dur           "0.3s"
                          :begin         "0s"}]]
     [:polygon.body {:points "0 -1  -1 1  0 0.5  1 1"}]]))

(defn bullet-pane []
  (let [bullets @(re-frame/subscribe [::subs/bullets])]
    [:g.bullets
     (for [{[x y]:position
            id :id} bullets]
       ^{:key [:bullet id]}
       [:g.bullet
        {:transform (str "translate("x " " y")")}
        [:circle.body {:cx 0 :cy 0 :r 0.15}]])]))

(defn asteroid-pane []
  (let [asteroids @(re-frame/subscribe [::subs/asteroids])]
    [:g.asteroid-plane
     (for [{[x y]:position
            size :size
            id :id} asteroids]
       ^{:key [:asteroid id]}
       [:g.asteroid
        {:transform (str "translate("x " " y")")}
        [:circle.body {:cx 0 :cy 0 :r size}]])]))


(defn world []
   [:g.world
    [:rect.background
     {:width  (config/dimensions 0)
      :height (config/dimensions 1)}]
    [bullet-pane]
    [spaceship]
    [asteroid-pane]])

(defn timer []
  (let [timer (re-frame/subscribe [::subs/time])]
    [:text {:x 0.1 :y 0.5 :font-size 0.4 :fill "white"}
     (str "Time: " @timer)]))

(defn score []
  (let [score (re-frame/subscribe [::subs/score])]
    [:text {:x 0.1 :y 1 :font-size 0.4 :fill "white"}
     (str "Score: " @score)]))

(defn game-over []
  (let [paused? @(re-frame/subscribe [::subs/paused?])]
    (when paused?
      [:rect.paused
       {:width  (config/dimensions 0)
        :height (config/dimensions 1)}]
      [:text {:x         (/ (config/dimensions 0) 2)
              :y         (/ (config/dimensions 1) 2)
              :font-size 5 :fill "white"}
       "Paused"])))

(defn ui-pane []
  [:g.ui
   [timer]
   [score]
   [game-over]])


(defn main-panel []
  [:div
   [:svg
    {:viewBox [0 0 (config/dimensions 0) (config/dimensions 1)]}
    [world]
    [ui-pane]]])
