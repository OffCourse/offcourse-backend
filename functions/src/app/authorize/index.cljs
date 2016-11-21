(ns app.authorize.index
  (:require [app.authorize.mappings :refer [mappings]]
            [app.authorize.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :as qa])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn initialize-service [raw-event raw-context cb]
  (service/initialize {:service-name :authorize
                       :callback     cb
                       :context      raw-context
                       :specs        specs/actions
                       :mappings     mappings
                       :event        raw-event
                       :adapters     [:db :auth :iam]}))

(defn authorize [& args]
  (go
    (let [{:keys [event] :as service}   (apply initialize-service args)
          {:keys [error] :as query}     (async/<! (ac/perform service [:verify event]))
          {:keys [found error] :as res} (when-not error (async/<! (qa/fetch service query)))
          auth-data                     (merge event query found)
          policy                        (ac/perform service [:create auth-data])]
      (service/done service policy))))
