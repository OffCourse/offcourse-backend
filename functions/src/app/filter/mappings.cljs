(ns app.filter.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [shared.protocols.queryable :as qa]
            [shared.protocols.actionable :as ac]
            [shared.protocols.loggable :as log]
            [clojure.string :as str]
            [shared.protocols.convertible :as cv]
            [cljs.nodejs :as node]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def marshaler (node/require "dynamodb-marshaler"))
(def unmarshal-item (.-unmarshalItem marshaler))

(defn not-found? [url urls]
  (not (contains? urls url)))

(defn mappings []
  (defmethod perform [:filter :bookmarks] [{:keys [db stage]} [_ payload]]
    (go
      (let [query (cv/to-query payload)
            table-name (str "resources-" stage)
            res (async/<! (qa/fetch db table-name query))
            urls (into #{} (map :resource-url (:found res)))]
        (filter #(not (contains? urls (:resource-url %))) payload))))

  (defmethod perform [:put :bookmarks] [{:keys [stream stage]} action]
    (ac/perform stream (str "missing-resources-" stage) action)))
