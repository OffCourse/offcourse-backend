(ns app.retrieve.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn mappings []

  (defmethod fetch :bucket-items [{:keys [bucket]} query]
    (go
      (let [{:keys [found errors]} (async/<! (qa/fetch bucket query))]
        {:found (when-not (empty? found) found)
         :error (when-not (empty? errors) errors)})))

  (defmethod perform [:put :nothing] [{:keys [stream stage]} action]
    (go
      {:error :empty-payload}))

  (defmethod perform :default [{:keys [stream stage]} action]
    (ac/perform stream action)))
