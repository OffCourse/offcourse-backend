(ns app.debug.index
  (:require [cljs.nodejs :as node]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [backend-shared.index]
            [shared.protocols.loggable :as log]
            [backend-shared.aws-event.index :as aws-event]
            [clojure.string :as str]))

(defn debug [raw-event context cb]
  (log/log "INCOMING:" raw-event)
  (let [event (aws-event/create raw-event)
        payload (cv/to-payload event)]
    (cb nil (clj->js payload))))
