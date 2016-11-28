(ns app.index.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [shared.protocols.queryable :as qa]
            [shared.protocols.actionable :as ac]
            [shared.protocols.loggable :as log]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn mappings []

  (defmethod perform [:put :profiles] [{:keys [index]} [_ payload]]
    (go
      (let [payload (map (fn [{:keys [:user-name] :as item}] [user-name item]) payload)
            {:keys [saved errors] :as r} (async/<! (ac/perform index "profiles" [:put payload]))]
        (log/log "X" (clj->js r))
        {:saved (when-not (empty? saved) saved)
         :error (when-not (empty? errors) errors)})))

  (defmethod perform [:put :courses] [{:keys [index]} [_ payload]]
    (go
      (let [payload (map (fn [{:keys [:course-id] :as item}] [course-id item]) payload)
            {:keys [saved errors]} (async/<! (ac/perform index "courses" [:put payload]))]
        {:saved (when-not (empty? saved) saved)
         :error (when-not (empty? errors) errors)})))

  (defmethod perform [:put :resources] [{:keys [index]} [_ payload]]
    (go
      (let [payload (map (fn [{:keys [:resource-url] :as item}] [(-> resource-url hash str) item]) payload)
            {:keys [saved errors]} (async/<! (ac/perform index "resources" [:put payload]))]
        {:saved (when-not (empty? saved) saved)
         :error (when-not (empty? errors) errors)}))))
