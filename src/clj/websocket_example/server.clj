(ns websocket-example.server
  (:require [websocket-example.handler :refer [app]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

 (defn -main [& args]
   (let [port (Integer/parseInt (or (env :port) "3449"))]
     (run-server app {:port port :join? false})))
