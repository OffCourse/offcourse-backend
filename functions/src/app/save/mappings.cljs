(ns app.save.mappings
  (:require [backend-shared.service.index :refer [perform]]
            [shared.protocols.actionable :as ac]
            [shared.protocols.specced :as sp]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]))

(defn mappings []
  (defmethod perform :default [{:keys [db table-names]} action]
    (ac/perform db  (cv/to-db action table-names))))
