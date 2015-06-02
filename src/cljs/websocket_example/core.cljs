(ns websocket-example.core
    (:require [reagent.core :as reagent :refer [atom]]))

(def message (atom nil))
;; -------------------------
;; Views

(defn welcome-message [message]
  [:div @message])

(defn home-page []
  [:div [:h2 "Websocket Example"]
   (if (not (nil? @message)) [welcome-message message])])


;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app"))
  (let [ws (js/WebSocket. "ws://localhost:3449/test")]
    (aset ws "onmessage" (fn [m] (swap! message (fn [] (aget m "data"))))))
  )

(defn init! []
  (mount-root))
