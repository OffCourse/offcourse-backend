(ns app.save.index
  (:require [app.save.mappings :refer [mappings]]
            [app.save.specs :refer [specs]]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def adapters {:db {:table-names {:identities         (.. js/process -env -identitiesTable)
                                  :courses            (.. js/process -env -coursesTable)
                                  :resources          (.. js/process -env -resourcesTable)
                                  :bookmarks          (.. js/process -env -bookmarksTable)
                                  :profiles           (.. js/process -env -profilesTable)}}})

(defn save [& args]
  (go
    (let [{:keys [event] :as service} (apply service/create specs mappings adapters args)
          payload                     (cv/to-payload event)
          {:keys [error success]} (async/<! (ac/perform service [:put payload]))]
      (when error
        (service/fail service {:error error}))
      (when success
        (service/done service {:saved success})))))
