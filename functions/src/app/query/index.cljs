(ns app.query.index
  (:require [app.query.mappings :refer [mappings]]
            [app.query.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :as qa])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn initialize-service [raw-event raw-context cb]
  (service/initialize {:service-name :query
                       :callback     cb
                       :context      raw-context
                       :specs        specs/actions
                       :mappings     mappings
                       :event        raw-event
                       :adapters     [:index]}))

(defn query [& args]
  (go
    (let [{:keys [query] :as service}     (apply initialize-service args)
          {:keys [found not-found error]} (async/<! (qa/fetch service query))]
      (when error
        (service/fail service error))
      (if not-found
        (service/done service {:not-found query})
        (service/done service found)))))
