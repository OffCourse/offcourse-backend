(ns app.download.index
  (:require [app.download.mappings :refer [mappings]]
            [app.download.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn download [raw-event context cb]
  (log/log raw-event)
  (go
    (let [service                           (service/create :download cb [:bucket :http :github :embedly]
                                                            mappings specs/actions)
          payload                           (cv/to-payload raw-event)
          {:keys [imported errors] :as res} (async/<! (ac/perform service [:download payload]))
          res                               (async/<! (ac/perform service [:put imported]))]
      (service/done service res))))
