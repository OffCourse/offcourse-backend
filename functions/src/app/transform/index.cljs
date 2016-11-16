(ns app.transform.index
  (:require [cljs.nodejs :as node]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :as qa]
            [shared.protocols.specced :as sp]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [backend-shared.index]
            [app.transform.specs :as specs]
            [app.transform.mappings :refer [mappings]]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn transform [raw-event context cb]
  (log/log raw-event)
  (go
    (let [service   (service/create :transform cb [:stream] mappings specs/actions)
          payload   (-> raw-event cv/to-payload)
          payloads  (ac/perform service [:transform payload])
          ops-chans (async/merge (map #(ac/perform service [:put %]) (vals payloads)))
          res       (async/<! (async/into [] ops-chans))]
      (service/done service payloads))))
