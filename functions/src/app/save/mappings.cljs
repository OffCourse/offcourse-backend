(ns app.save.mappings
  (:require [backend-shared.service.index :refer [perform]]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]))

(defn mappings []
  (defmethod perform :default [{:keys [db stage]} action]
    (ac/perform db  (cv/to-db action))))
