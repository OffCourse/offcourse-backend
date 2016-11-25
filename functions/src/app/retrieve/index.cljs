(ns app.retrieve.index
  (:require [app.retrieve.mappings :refer [mappings]]
            [app.retrieve.specs :as specs]
            [backend-shared.service.index :as service]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn initialize-service [raw-event raw-context cb]
  (service/initialize {:service-name :retrieve
                       :callback     cb
                       :context      raw-context
                       :specs        specs/actions
                       :mappings     mappings
                       :event        raw-event
                       :adapters     [:bucket :stream]}))

(defn retrieve [& args]
  (go
    (let [{:keys [event] :as service} (apply initialize-service args)
          query                       (cv/to-query event)
          {:keys [found error]}       (async/<! (qa/fetch service query))
          {:keys [success error]}     (async/<! (ac/perform service [:put found]))]
      (service/done service (or success error)))))
