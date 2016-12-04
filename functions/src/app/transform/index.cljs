(ns app.transform.index
  (:require [cljs.nodejs :as node]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :as qa]
            [shared.protocols.specced :as sp]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [backend-shared.index]
            [app.transform.specs :refer [specs]]
            [app.transform.mappings :refer [mappings]]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [cljs.spec :as spec]
            [shared.models.payload.index :as payload])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def adapters {:stream {:stream-names {:bookmarks          (.. js/process -env -bookmarksStream)
                                       :courses            (.. js/process -env -coursesStream)
                                       :raw-github-courses (.. js/process -env -rawGithubCoursesStream)
                                       :resources          (.. js/process -env -resourcesStream)
                                       :profiles           (.. js/process -env -profilesStream)
                                       :raw-portraits      (.. js/process -env -rawPortraitsStream)
                                       :identities         (.. js/process -env -identitiesStream)
                                       :errors             (.. js/process -env -errorsStream)}}})

(defn transform [& args]
  (go
    (let [{:keys [event] :as service}   (apply service/create specs mappings adapters args)
          payload                       (cv/to-payload event)
          payloads                      (ac/perform service [:transform payload])
          ops-chans                     (async/merge (map #(ac/perform service [:put %])
                                                          (vals payloads)))
          res                           (async/<! (async/into [] ops-chans))]
      (service/done service res))))
