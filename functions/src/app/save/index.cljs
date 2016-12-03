(ns app.save.index
  (:require [app.save.mappings :refer [mappings]]
            [app.save.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [cljs.nodejs :as node]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(node/enable-util-print!)

(def table-names  {:identities         (.. js/process -env -identitiesTable)
                   :courses            (.. js/process -env -coursesTable)
                   :resources          (.. js/process -env -resourcesTable)
                   :bookmarks          (.. js/process -env -bookmarksTable)
                   :profiles           (.. js/process -env -profilesTable)})

(def environment {:table-names table-names})

(defn initialize-service [raw-event raw-context cb]
  (service/initialize {:service-name :save
                       :callback     cb
                       :context      raw-context
                       :specs        specs/actions
                       :mappings     mappings
                       :event        raw-event
                       :environment  environment
                       :adapters     [:db]}))

(defn save [& args]
  (go
    (let [{:keys [event] :as service} (apply initialize-service args)
          payload                     (cv/to-payload event)
          {:keys [error success]} (async/<! (ac/perform service [:put payload]))]
      (when error
        (service/fail service {:error error}))
      (when success
        (service/done service {:saved success})))))
