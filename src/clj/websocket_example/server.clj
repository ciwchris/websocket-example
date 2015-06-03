(ns websocket-example.server
  (:require [websocket-example.handler :refer [app]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3449"))]
     (run-server app {:port port :join? false})))
