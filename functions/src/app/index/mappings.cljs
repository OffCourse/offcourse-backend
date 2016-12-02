(ns app.index.mappings
  (:require [backend-shared.service.index :refer [perform]]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn mappings []

  (defmethod perform :default [{:keys [index]} action]
    (go
      (let [{:keys [saved errors]} (async/<! (ac/perform index action))]
        {:saved (when-not (empty? saved) saved)
         :error (when-not (empty? errors) errors)}))))
