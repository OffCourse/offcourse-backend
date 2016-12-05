(ns app.augment.mappings
  (:require [backend-shared.service.index :refer [perform fetch]]
            [shared.protocols.actionable :as ac]
            [shared.protocols.queryable :as qa]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn mappings []

  (defmethod fetch :bookmarks [{:keys [db]} payload]
    (let [query (map (fn [{:keys [bookmark-url]}] {:resource-url bookmark-url}) payload)]
      (qa/fetch db query)))

  (defmethod perform [:put :nothing] [_ _]
    (go {:error :no-payload}))

  (defmethod perform [:put :errors] [_ [_ errors]]
    (go errors))

  (defmethod perform :default [{:keys [bucket]} action]
    (ac/perform bucket action)))
