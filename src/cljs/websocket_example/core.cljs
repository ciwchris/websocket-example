(ns websocket-example.core
    (:require [reagent.core :as reagent :refer [atom]]))

(def message (atom nil))
;; -------------------------
;; Views


(defn home-page []
  [:div [:h2 "Websocket Example"]
   (if (not (nil? @message)) [:div @message])])


;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app"))
  (let [ws (js/WebSocket. "ws://localhost:3449/message")]
    (aset ws "onmessage" (fn [m] (swap! message (fn [] (aget m "data"))))))
  )

(defn init! []
  (mount-root))
