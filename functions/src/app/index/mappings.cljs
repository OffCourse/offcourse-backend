(ns app.index.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [shared.protocols.queryable :as qa]
            [shared.protocols.actionable :as ac]
            [shared.protocols.loggable :as log]))

(defn mappings []


  (defmethod perform [:put :profiles] [{:keys [index]} [_ payload]]
    (let [payload (map (fn [{:keys [:user-name] :as item}] [user-name item]) payload)]
      (ac/perform index "profiles" [:put payload])))

  (defmethod perform [:put :courses] [{:keys [index]} [_ payload]]
    (let [payload (map (fn [{:keys [:course-id] :as item}] [course-id item]) payload)]
      (ac/perform index "courses" [:put payload])))

  (defmethod perform [:put :resources] [{:keys [index]} [_ payload]]
    (let [payload (map (fn [{:keys [:resource-url] :as item}] [(-> resource-url hash str) item]) payload)]
      (ac/perform index "resources" [:put payload]))))
