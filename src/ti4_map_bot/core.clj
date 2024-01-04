;; SPDX-License-Identifier: MIT OR 0BSD
(ns ti4-map-bot.core
  (:require
   [clojure.data.json :as json]
   [clojure.edn :as edn]
   [clojure.core.async :refer [chan close!]]
   [discljord.messaging :as discord-rest]
   [discljord.connections :as discord-ws]
   [discljord.events :refer [message-pump!]]
   [ring.util.response :refer [response]]
   [org.httpkit.server :refer [run-server]]
   [compojure.core :refer :all]
    )
  (:import
   [ti4.map GameSaveLoadManager]))

(def state (atom nil))
(def bot-id (atom nil))
(def config (edn/read-string (slurp "config.edn")))

(defmulti handle-event
  "Event handling multi method. Dispatches on the type of the event."
  (fn [type _data] type))

(defmethod handle-event :ready
  [_ _]
  (discord-ws/status-update! (:gateway @state) :activity (discord-ws/create-activity :name (:playing config))))

(defmethod handle-event :default [_ _data]
  )

(defn start-bot!
  "Start a discord bot using the token specified in `config.edn`.

  Returns a map containing the event channel (`:events`), the gateway connection (`:gateway`) and the rest connection (`:rest`)."
  [token & intents]
  (let [event-channel (chan 100)
        gateway-connection (discord-ws/connect-bot! token event-channel :intents (set #{:guilds :guild-messages}))
        rest-connection (discord-rest/start-connection! token)]
    {:events  event-channel
     :gateway gateway-connection
     :rest    rest-connection}))

(defn stop-bot!
  "Takes a state map as returned by [[start-bot!]] and stops all the connections and closes the event channel."
  [{:keys [rest gateway events] :as _state}]
  (discord-rest/stop-connection! rest)
  (discord-ws/disconnect-bot! gateway)
  (close! events))

(defn get-user-name [id]
  (response (json/write-str @(discord-rest/get-guild-member! (:rest @state) (:server config) id))))

(defroutes web-handler
  (GET "/user/:id" [id] (get-user-name id))
  )


(defn -main [& args]
  (reset! state (start-bot! (:token config) ))
  (reset! bot-id (:id @(discord-rest/get-current-user! (:rest @state))))
  (run-server web-handler {:port 3000})
  (try
    (message-pump! (:events @state) handle-event)
    (finally (stop-bot! @state))))

