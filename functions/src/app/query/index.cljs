(ns app.query.index
  (:require [app.query.mappings :refer [mappings]]
            [app.query.specs :refer [specs]]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def adapters {:index {:search-url (.. js/process -env -elasticsearchEndpoint)}})

(defn query [& args]
  (go
    (let [{:keys [event] :as service}     (apply service/create specs mappings adapters args)
          query                           (cv/to-query event)
          {:keys [found not-found error]} (async/<! (qa/fetch service query))]
      (when error
        (service/fail service error))
      (if not-found
        (service/done service {:not-found query})
        (service/done service found)))))
