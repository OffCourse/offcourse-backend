(ns app.command.mappings
  (:require [backend-shared.service.index :refer [fetch perform]]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [app.command.messages :as messages]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp]
            [shared.protocols.queryable :as qa])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn create-auth-id [{:keys [user_id provider]}]
  (str provider "|" user_id))

(defn create-auth-ids [{:keys [identities]}]
  (->> identities
       (map create-auth-id)
       (into #{})))

(defn check-auth [user auth-id]
  (let [{:keys [auth-profile user-name]} user
        auth-ids                         (create-auth-ids auth-profile)]
    (when-not (contains? auth-ids auth-id) :invalid-user)))

(defn check-curator-and-repo [course repository user-name]
  (cond (not= "offcourse" (:repository course)) :not-repository
        (not= user-name (:curator course)) :not-curator))

(defn user-s3-item [user]
  {:item-key (:user-name user)
   :item-data (->> user clj->js (.stringify js/JSON))})

(defn mappings []

  (defmethod fetch :identity [{:keys [db table-names]} query]
    (qa/fetch db query))

  (defmethod perform [:sign-up :raw-user :guest] [{:keys [bucket stage]} [_ user :as action]]
    (go
      (if-let [error (check-auth user (-> action meta :guest :auth-id))]
        {:denied (error messages/errors)}
        (do
          (async/<! (ac/perform bucket (cv/to-bucket [:put [user]])))
          {:accepted user}))))

  (defmethod perform [:sign-in nil :guest] [{:keys [stream stage]} action]
    (go {:denied (:not-signed-up messages/errors)}))

  (defmethod perform [:sign-in nil :user] [{:keys [stream stage]} action]
    (go {:accepted (select-keys (-> action meta :user) [:user-name])}))


  (defmethod perform [:sign-up :raw-user :user] [{:keys [bucket stage]} [_ user :as action]]
    (go
      {:denied "user already exists"}))

  (defmethod perform [:import :github-repo :user] [{:keys [stream]} [_ repo :as action]]
    (let [user-name (-> action meta :user :user-name)]
      (ac/perform stream (cv/to-stream [:put [(assoc repo :user-name user-name)]]))))

  (defmethod perform [:add :course :user] [{:keys [bucket]} [_ course :as action]]
    (go
      (if-let [error (check-curator-and-repo course "offcourse" (-> action meta :user :user-name))]
        (do
          (log/log "error" error)
          {:denied (error messages/errors)})
        (do
          (async/<! (ac/perform bucket (cv/to-bucket [:put [course]])))
          {:accepted course}))))

  (defmethod perform :default [{:keys [stream stage]} action]
    (if (:user (meta action))
      (go {:denied :invalid-action})
      (go {:denied (:not-signed-in messages/errors)}))))
