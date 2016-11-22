(ns app.authorize.index
  (:require [app.authorize.mappings :refer [mappings]]
            [app.authorize.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :as qa]
            [shared.protocols.convertible :as cv])
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
    (let [{:keys [event] :as service} (apply initialize-service args)
          credentials                 (cv/to-payload event)
          {:keys [error] :as query}   (async/<! (ac/perform service [:verify credentials]))
          {:keys [found error]}       (async/<! (qa/fetch service query))
          updated-credentials         (merge credentials query found)
          {:keys [policy]}            (ac/perform service [:create updated-credentials])]
      (service/done service policy))))
