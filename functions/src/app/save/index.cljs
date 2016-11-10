(ns app.save.index
  (:require [app.save.mappings :refer [mappings]]
            [app.save.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [cljs.nodejs :as node]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(node/enable-util-print!)

(defn save [raw-event context cb]
  (log/log raw-event)
  (go
    (let [service                         (service/create :save cb [:db :bucket]
                                                          mappings specs/actions)
          payload                         (flatten (cv/to-payload raw-event))
          {:keys [error success]}         (async/<! (ac/perform service [:put payload]))]
      (when error
        (service/fail service {:error error}))
      (when success
        (service/done service {:saved payload})))))
