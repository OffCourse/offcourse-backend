(ns app.command.index
  (:require [app.command.mappings :refer [mappings]]
            [app.command.specs :refer [specs]]
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
          {:keys [found] :as r}       (when-not (= provider "offcourse")
                                        (async/<! (qa/fetch service {:auth-id principalId})))]
      (if (or (= provider "offcourse") (:user-name found))
        {:user {:user-name (or (-> found :user-name) user-name)}}
        {:guest {:auth-id principalId}}))))

(def adapters {:bucket  {:bucket-names {:courses     (.. js/process -env -rawCoursesBucket)
                                        :raw-users   (.. js/process -env -rawUsersBucket)}}
               :stream  {:stream-names {:raw-repos   (.. js/process -env -rawReposStream)}}
               :db      {:table-names  {:identity    (.. js/process -env -identitiesTable)}}})

(defn command [& args]
  (go
    (let [{:keys [event] :as service}     (apply service/create specs mappings adapters args)
          action                          (cv/to-action event)
          user                            (async/<! (guest-or-user service event))
          {:keys [accepted denied error]} (async/<! (ac/perform service (with-meta action user)))]
      (when denied    (service/unauthorized service denied))
      (when accepted (service/accepted service accepted))
      (service/fail service (or error "unknown-error")))))
