(ns app.query.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [shared.protocols.queryable :as qa]
            [shared.protocols.actionable :as ac]
            [shared.protocols.loggable :as log]
            [shared.protocols.convertible :as cv]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn handle-request [index index-name query single]
  (go
    (let [request (qa/fetch index index-name (cv/to-search query))
          {:keys [found] :as res} (async/<! request)]
      (if-not (empty? found)
        (assoc res :found (if single (first found) found))
        (assoc res :not-found query)))))

(defn mappings []
  (defmethod fetch :collection [{:keys [index stage]} query]
    (handle-request index "courses" query false))

  (defmethod fetch :course [{:keys [index stage]} query]
    (handle-request index "courses" query true))

  (defmethod fetch :resource [{:keys [index stage]} query]
    (handle-request index "resources" query true)))
