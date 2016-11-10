(ns app.command.index
  (:require [app.command.guest.index :as guest]
            [app.command.user.index :as user]
            [app.command.mappings :refer [mappings]]
            [backend-shared.aws-event.index :as aws-event]
            [cljs.nodejs :as node]
            [clojure.string :as str]
            [shared.protocols.loggable :as log]
            [shared.protocols.convertible :as cv]
            [backend-shared.service.index :as service]
            [shared.protocols.queryable :as qa]
            [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(node/enable-util-print!)

(defn guest-or-user [service raw-event]
  (go
    (let [{:keys [principalId]} (aws-event/create raw-event)
          [provider user-name]    (str/split principalId "|")
          {:keys [found]}       (when-not (= provider "offcourse")
                                  (async/<! (qa/fetch service {:auth-id principalId})))]
      (if (or (= provider "offcourse") (:user-name found))
        {:user {:user-name (or (-> found :user-name) user-name)}}
        {:guest {:auth-id principalId}}))))

(defn command [raw-event context cb]
  (log/log raw-event)
  (go
    (let [service              (service/create :command-portal cb [:db] mappings identity)
          action               (cv/to-action raw-event)
          {:keys [guest user]} (async/<! (guest-or-user service raw-event))]
      (if user
        (user/user-flow (with-meta action user) cb)
        (guest/guest-flow (with-meta action guest) cb)))))
