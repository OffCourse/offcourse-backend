(ns app.retrieve.index
  (:require [app.retrieve.mappings :refer [mappings]]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :as qa]
            [shared.protocols.actionable :as ac]
            [app.retrieve.specs :as specs])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn retrieve [raw-event context cb]
  (log/log raw-event)
  (go
    (let [service         (service/create :retrieve cb [:bucket :stream] mappings specs/actions)
          query           (cv/to-query raw-event)
          {:keys [found]} (async/<! (qa/fetch service query))
          res             (async/<! (ac/perform service [:put found]))]
      (service/done service found))))
