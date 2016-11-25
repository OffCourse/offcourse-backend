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


(defn initialize-service [raw-event raw-context cb]
  (service/initialize {:service-name :transform
                       :callback     cb
                       :context      raw-context
                       :specs        specs/actions
                       :mappings     mappings
                       :event        raw-event
                       :adapters     [:stream]}))

(defn transform [& args]
  (go
    (let [{:keys [event] :as service}   (apply initialize-service args)
          payload                       (cv/to-payload event)
          payloads                      (ac/perform service [:transform payload])
          ops-chans                     (async/merge (map #(ac/perform service [:put %]) (vals payloads)))
          res                           (async/<! (async/into [] ops-chans))]
      (service/done service res))))
