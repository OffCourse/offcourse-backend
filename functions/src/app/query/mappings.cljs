(ns app.query.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [shared.protocols.queryable :as qa]
            [shared.protocols.actionable :as ac]
            [shared.protocols.loggable :as log]
            [shared.protocols.convertible :as cv]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn mappings []
  (defmethod fetch :collection [{:keys [index stage]} query]
    (qa/fetch index "courses" (cv/to-search query)))

  (defmethod fetch :course [{:keys [index stage]} query]
    (go
      (when-let [{:keys [found]} (async/<! (qa/fetch index "courses" (cv/to-search query)))]
        {:found (first found)})))

  (defmethod fetch :resource [{:keys [index stage]} query]
    (go
      (when-let [{:keys [found]} (async/<! (qa/fetch index "resources" (cv/to-search query)))]
        {:found (first found)})))

  (defmethod perform [:put :bookmark] [{:keys [stream stage]} action]
    (ac/perform stream (str "bookmarks-" stage) action)))
