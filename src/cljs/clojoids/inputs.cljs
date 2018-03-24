(ns clojoids.inputs
  (:require [goog.events :as events]
            [re-frame.core :refer [dispatch]])
  (:import [goog.events EventType]))

(def keymap {"ArrowLeft"  :left
             "a"          :left
             "ArrowUp"    :up
             "w"          :up
             "ArrowRight" :right
             "d"          :right
             "ArrowDown"  :down
             "s"          :down
             " "          :shoot
             "p"          :pause})

(defn dispatch-key [event keyevent down?]
  (when-let [key (keymap (.-key keyevent))]
    (dispatch [event key down?])))

(defn listen-to-keys []
  (events/listen js/window EventType.KEYDOWN #(dispatch-key :clojoids.events/key-down % true))
  (events/listen js/window EventType.KEYUP #(dispatch-key :clojoids.events/key-down % false)))