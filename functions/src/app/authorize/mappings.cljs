(ns app.authorize.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa]
            [shared.protocols.loggable :as log]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn mappings []

  (defmethod fetch :identity [{:keys [db table-names]} query]
    (qa/fetch db query))

  (defmethod fetch :error [{:keys [db]} error]
    (go error))

  (defmethod perform [:verify :credentials] [{:keys [auth]} action]
    (ac/perform auth action))

  (defmethod perform [:create :credentials] [{:keys [iam]} action]
    (ac/perform iam action)))
