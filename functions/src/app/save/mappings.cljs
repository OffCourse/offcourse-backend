(ns app.save.mappings
  (:require [backend-shared.service.index :refer [perform]]
            [shared.protocols.actionable :as ac]
            [shared.protocols.specced :as sp]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]))

(defn mappings []
  (defmethod perform :default [{:keys [db table-names]} action]
    (let [payload-type (second (sp/resolve action))
          table-name (payload-type table-names)]
      (ac/perform db  (cv/to-db action table-name)))))
