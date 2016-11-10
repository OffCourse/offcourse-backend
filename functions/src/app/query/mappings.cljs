(ns app.query.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [shared.protocols.queryable :as qa]
            [shared.protocols.actionable :as ac]
            [shared.protocols.loggable :as log]
            [shared.protocols.convertible :as cv]))

(defn mappings []
  (defmethod fetch :collection [{:keys [index stage]} query]
    (qa/fetch index "courses" query))

  (defmethod fetch :course [{:keys [index stage]} query]
    (qa/fetch index "courses" query))

  (defmethod fetch :resource [{:keys [db stage]} query]
    (qa/fetch db (str "resources-" stage) (select-keys query [:resource-url])))

  (defmethod perform [:put :bookmark] [{:keys [stream stage]} action]
    (ac/perform stream (str "bookmarks-" stage) action)))
