(ns app.index.index
  (:require [app.index.mappings :refer [mappings]]
            [app.index.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn index [raw-event context cb]
  (log/log raw-event)
  (go
    (let [service         (service/create :save cb [:index] mappings specs/actions)
          {:keys [added]} (cv/to-events raw-event)
          res             (when added (async/<! (ac/perform service [:put added])))]
      (service/done service res))))
