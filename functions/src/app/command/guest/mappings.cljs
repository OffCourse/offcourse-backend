(ns app.command.guest.mappings
  (:require [backend-shared.service.index :refer [perform]]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [app.command.messages :as messages]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn create-auth-id [{:keys [user_id provider]}]
  (str provider "|" user_id))

(defn create-auth-ids [{:keys [identities]}]
  (->> identities
       (map create-auth-id)
       (into #{})))

(defn check [user auth-id]
  (let [{:keys [auth-profile user-name]} user
        auth-ids                         (create-auth-ids auth-profile)]
    (when-not (contains? auth-ids auth-id) :invalid-user)))

(defn user-s3-item [user]
  {:item-key (:user-name user)
   :item-data (->> user clj->js (.stringify js/JSON))})

(defn mappings []
  (defmethod perform [:sign-in nil] [{:keys [stream stage]} action]
    (go {:denied (:not-signed-up messages/errors)}))

  (defmethod perform [:sign-up :raw-user] [{:keys [bucket stage]} [_ user :as action]]
    (go
      (if-let [error (check user (-> action meta :auth-id))]
        {:denied (error messages/errors)}
        (do
          (async/<! (ac/perform bucket (cv/to-bucket [:put [user]])))
          {:accepted user}))))

  (defmethod perform :default [{:keys [stream stage]} action]
    (go {:denied (:not-signed-in messages/errors)})))
