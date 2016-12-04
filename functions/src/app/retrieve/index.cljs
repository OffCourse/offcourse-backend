(ns app.retrieve.index
  (:require [app.retrieve.mappings :refer [mappings]]
            [app.retrieve.specs :refer [specs]]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def adapters {:stream {:stream-names {:raw-users          (.. js/process -env -rawUsersStream)
                                       :courses            (.. js/process -env -coursesStream)
                                       :raw-resources      (.. js/process -env -rawResourcesStream)
                                       :github-repos       (.. js/process -env -githubReposStream)
                                       :github-courses     (.. js/process -env -githubCoursesStream)
                                       :errors             (.. js/process -env -errorsStream)}}
               :bucket {:bucket-names {}}})

(defn retrieve [& args]
  (go
    (let [{:keys [event] :as service} (apply service/create specs mappings adapters args)
          query                       (cv/to-query event)
          {:keys [found error]}       (async/<! (qa/fetch service query))
          {:keys [accepted error]}    (async/<! (ac/perform service [:put found]))]
      (service/done service (or accepted error)))))
