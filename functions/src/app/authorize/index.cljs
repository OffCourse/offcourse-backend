(ns app.authorize.index
  (:require [app.authorize.mappings :refer [mappings]]
            [app.authorize.specs :refer [specs]]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def adapters {:db   {:table-names {:identity (.. js/process -env -identitiesTable)}}
               :auth {:api-keys {:auth0 (.. js/process -env -authSecret)}}
               :iam  {}})

(defn authorize [& args]
  (go
    (let [{:keys [event] :as service}        (apply service/create specs mappings adapters args)
          credentials                        (cv/to-payload event)
          {:keys [error] :as query}          (async/<! (ac/perform service [:verify credentials]))
          {:keys [found error]}              (async/<! (qa/fetch service query))
          updated-credentials                (merge credentials query found)
          {:keys [policy]}                   (ac/perform service [:create updated-credentials])]
      (service/done service policy))))
