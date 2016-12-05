(ns app.augment.index
  (:require [app.augment.mappings :refer [mappings]]
            [app.augment.specs :refer [specs]]
            [backend-shared.service.index :as service]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def adapters {:db {:table-names {:bookmarks (.. js/process -env -bookmarksTable)}}})

(defn augment [& args]
  (go
    (let [{:keys [event] :as service} (apply service/create specs mappings adapters args)
          {:keys [added] :as payload} (cv/to-db-events event)
          res                         (async/<! (qa/fetch service added))]
      (service/done service res))))
