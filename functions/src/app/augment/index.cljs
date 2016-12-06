(ns app.augment.index
  (:require [app.augment.mappings :refer [mappings]]
            [app.augment.specs :refer [specs]]
            [backend-shared.service.index :as service]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def adapters {:db {:table-names {:bookmarks (.. js/process -env -bookmarksTable)
                                  :courses   (.. js/process -env -coursesTable)}}})

(defn augment [& args]
  (go
    (let [{:keys [event] :as service} (apply service/create specs mappings adapters args)
          {:keys [added] :as payload} (cv/to-db-events event)
          {:keys [found errors]}      (async/<! (qa/fetch service added))
          res                         (async/<! (ac/perform service [:transform found]))]
      (service/done service res))))
