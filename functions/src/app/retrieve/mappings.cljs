(ns app.retrieve.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn mappings []

  (defmethod fetch :bucket-items [{:keys [bucket]} query]
    (go {:found (async/<! (qa/fetch bucket query))}))

  (defmethod perform :default [{:keys [stream stage]} action]
    (ac/perform stream (cv/to-stream action))))
