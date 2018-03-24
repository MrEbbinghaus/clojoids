(ns clojoids.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [clojoids.events :as events]
            [clojoids.inputs :refer [listen-to-keys]]
            [clojoids.views :as views]
            [clojoids.config :as config])
  (:import [goog.events EventType]))

(defonce ticker (js/setInterval #(re-frame/dispatch [::events/tick]) 16))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (listen-to-keys)
  (mount-root))

