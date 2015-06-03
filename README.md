reagent-websocket-example
========================

Basic websocket example using [Reagent](https://github.com/reagent-project/reagent)
and [HTTP Kit](https://github.com/http-kit/http-kit).

To recreate
-----------

Use the [Leiningen template](https://github.com/reagent-project/reagent-template) to create a new project:

```
lein new reagent websocket-example
```

#### Make the following edits to project.clj

Add http-kit to the project dependencies:

```clj
:dependencies [[http-kit "2.1.18"]
  ...
  ]
```

Add a nrepl port to the figwheel settings, to easily connect to the repl
session using additional repls.

```clj
:figwheel {:http-server-root "public"
          :nrepl-port 7888
```

#### Make the following edits to handler.clj

Require http-kit and include the symbols to use:

```clj
(:require [[org.httpkit.server :refer [with-channel on-close send!]
  ...
  ])
```

Add an atom to store the connected client and the websocket configuration
for http-kit to use, see [http-kit documentation](http://www.http-kit.org/server.html#websocket).

```clj
(def clients (atom {}))

(defn ws
  [req]
  (with-channel req con
    (swap! clients assoc con true)
    (println con " connected")
    (on-close con (fn [status]
                    (swap! clients dissoc con)
                    (println con " disconnected. status: " status)))))
```

Add a route for websockets:

```clj
(defroutes routes
  (GET "/" [] home-page)
  (GET "/message" [] ws)
  (resources "/")
  (not-found "Not Found"))
```

Add a method which can be called in a repl to send a message from the server:

```clj
(defn write-message [message]
  (doseq [client @clients]
    (send! (key client) message false)))
```

#### Make the following edits to server.clj

Require http-kit instead of jetty and start the server using http-kit, see
[http-kit migration guide](http://www.http-kit.org/migration.html):

```clj
(:require [websocket-example.handler :refer [app]]
        [environ.core :refer [env]]
        [org.httpkit.server :refer [run-server]])
```

```clj
(defn -main [& args]
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-server app {:port port :join? false})))
```

#### Make the following edits to repl.clj

Similary, replace jetty with http-kit and use it to start and stop the server:

```clj
(:use websocket-example.handler
    websocket-example.dev
    org.httpkit.server
    [ring.middleware file-info file])
```

```clj
(defn stop-server []
  (@server)
  (reset! server nil))
```

#### Make the following edits to core.cljs

The file can be pared down to its essentials. Add an atom to hold messages received
from the server. Then connect to the websocket when the application is mounted and
add server messages to the atom when a websocket message is received.

```clj
(def message (atom nil))

(defn home-page []
  [:div [:h2 "Websocket Example"]
   (if (not (nil? @message)) [:div @message])])

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app"))
  (let [ws (js/WebSocket. "ws://localhost:3449/message")]
    (aset ws "onmessage" (fn [m] (swap! message (fn [] (aget m "data")))))))
```

Running the example
-------------------

Start the application:

```
lein do clean, figwheel
```

Open a browser and navigate to the page, [http://localhost:3449](http://localhost:3449).
Then start a new nrepl connecting to the nrepl session started by figwheel.

```
lein repl :connect 7888
```

In the repl enter the namespace of the handler and send a message to the connected browser:

```
(in-ns 'websocket-example.handler)
(write-message "Hello from server")
```

The message will be displayed in the browser underneath the title text.
