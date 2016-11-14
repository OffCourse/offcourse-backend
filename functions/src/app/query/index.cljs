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

(defn query [raw-event context cb]
  (log/log raw-event)
  (go
    (let [service                         (service/create :query cb [:index :stream]
                                                          mappings specs/actions)
          query                           (-> raw-event cv/to-query)
          {:keys [found error not-found] :as res} (async/<! (qa/fetch service query))]
      (service/done service found)
      (when error
        (service/fail service error))
      (when not-found
        #_(<! (ac/perform service [:put query])))
      (if found
        (service/done service found)
        (service/done service {:not-found query})))))
