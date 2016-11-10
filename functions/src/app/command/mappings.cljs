(ns app.command.mappings
  (:require [backend-shared.service.index :refer [fetch]]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa]))

(defn mappings []

  (defmethod fetch :identity [{:keys [db stage]} query]
    (qa/fetch db (cv/to-db query))))

