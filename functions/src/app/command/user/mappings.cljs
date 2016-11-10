(ns app.command.user.mappings
  (:require [backend-shared.service.index :refer [perform]]
            [app.command.messages :as messages]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn check [course repository user-name]
  (cond (not= "offcourse" (:repository course)) :not-repository
        (not= user-name (:curator course))      :not-curator))

(defn mappings []

  (defmethod perform [:sign-in nil] [_ action]
    (go {:accepted (select-keys (meta action) [:user-name])}))

  (defmethod perform [:add :course] [{:keys [bucket]} [_ course :as action]]
    (go
      (if-let [error (check course "offcourse" (-> action meta :user-name))]
        {:denied (error messages/errors)}
        (do
          (async/<! (ac/perform bucket (cv/to-bucket [:put [course]])))
          {:accepted course}))))

  (defmethod perform [:sign-up :raw-user] [_ action]
    (go {:denied "user already exists"}))

  (defmethod perform [:import :github-repo] [{:keys [stream]} [_ repo :as action]]
    (let [user-name (-> action meta :user-name)]
      (ac/perform stream (cv/to-stream [:put [(assoc repo :user-name user-name)]])))))
