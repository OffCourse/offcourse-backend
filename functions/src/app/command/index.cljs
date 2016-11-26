(ns app.command.index
  (:require [app.command.mappings :refer [mappings]]
            [app.command.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [clojure.string :as str]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn guest-or-user [service {:keys [principalId] :as event}]
  (go
    (let [[provider user-name]    (str/split principalId "|")
          {:keys [found]}       (when-not (= provider "offcourse")
                                  (async/<! (qa/fetch service {:auth-id principalId})))]
      (if (or (= provider "offcourse") (:user-name found))
        {:user {:user-name (or (-> found :user-name) user-name)}}
        {:guest {:auth-id principalId}}))))

(defn initialize-service [raw-event raw-context cb]
  (service/initialize {:service-name :command
                       :callback     cb
                       :context      raw-context
                       :specs        specs/actions
                       :mappings     mappings
                       :event        raw-event
                       :adapters     [:db :stream :bucket]}))

(defn command [& args]
  (go
    (let [{:keys [event callback] :as service} (apply initialize-service args)
          action                               (cv/to-action event)
          user                                 (async/<! (guest-or-user service event))
          {:keys [accepted denied error]}      (async/<! (ac/perform service (with-meta action user)))]
      (when denied    (service/unauthorized service denied))
      (when accepted (service/accepted service accepted))
      (service/fail service (or error "unknown-error")))))
