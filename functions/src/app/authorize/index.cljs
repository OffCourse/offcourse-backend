(ns app.authorize.index
  (:require [shared.protocols.loggable :as log]
            [backend-shared.service.index :as service]
            [shared.protocols.convertible :as cv]
            [backend-shared.index]
            [app.authorize.specs :as specs]
            [shared.specs.action :as action :refer [action-spec]]
            [app.authorize.mappings :refer [mappings]]
            [cljs.core.async :as async]
            [shared.protocols.queryable :as qa]
            [shared.protocols.actionable :as ac]
            [backend-shared.aws-event.index :as aws-event]
            [cljs.spec :as spec]
            [shared.specs.aws :as aws])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn  authorize [raw-event context cb]
  (log/log raw-event)
  (go
    (let [service         (service/create :authorize cb [:db :auth :iam] mappings specs/actions)
          auth-event      (aws-event/create raw-event)
          verification    (async/<! (ac/perform service [:verify (aws-event/create raw-event)]))
          {:keys [found]} (async/<! (qa/fetch service verification))
          policy          (ac/perform service [:create (merge auth-event verification found)])]
      (log/log "Policy:"  (clj->js policy))
      (service/done service policy))))
