(ns app.transform.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]
            [shared.specs.embedly :as embedly]
            [shared.specs.course :as course]
            [shared.specs.raw :as raw]
            [shared.specs.bookmark :as bookmark]
            [shared.specs.github :as github]
            [shared.specs.resource :as resource]
            [shared.specs.profile :as profile]
            [shared.specs.identity :as identity]))

(spec/def ::action-types action/types)

(defn actions []

  (defmethod action-spec :add [_]
    (spec/tuple ::action-types (spec/or :meta #{:meta}
                                        :id   #{:id})))

  (defmethod action-spec :index [_]
    (spec/tuple ::action-types (spec/or :checkpoints #{:checkpoints})))

  (defmethod action-spec :transform [_]
    (spec/tuple ::action-types (spec/or :embedly           (spec/coll-of ::embedly/resource)
                                        :github-repos      (spec/coll-of ::github/repo)
                                        :github-courses    (spec/coll-of ::github/course)
                                        :courses           (spec/coll-of ::course/course)
                                        :raw-users         (spec/coll-of ::raw/user)
                                        :raw-courses       (spec/coll-of ::raw/course))))

  (defmethod action-spec :put [_]
    (spec/tuple ::action-types (spec/or :bookmarks          (spec/coll-of ::bookmark/bookmark)
                                        :courses            (spec/coll-of ::course/course)
                                        :raw-github-courses (spec/coll-of ::github/course)
                                        :resources          (spec/coll-of ::resource/resource)
                                        :raw-resources      (spec/coll-of ::embedly/resource)
                                        :profiles           (spec/coll-of ::profile/profile)
                                        :raw-portraits      (spec/coll-of ::profile/portrait)
                                        :identities         (spec/coll-of ::identity/identity)
                                        :errors             any?))))
