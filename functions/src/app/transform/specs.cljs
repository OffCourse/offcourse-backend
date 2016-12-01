(ns app.transform.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(defn actions []

  (defmethod action-spec :add [_]
    (spec/tuple :offcourse/actions (spec/or :meta #{:meta}
                                            :id   #{:id})))

  (defmethod action-spec :index [_]
    (spec/tuple :offcourse/actions (spec/or :checkpoints #{:checkpoints})))

  (defmethod action-spec :transform [_]
    (spec/tuple :offcourse/actions (spec/or :embedly           (spec/coll-of :embedly/resource)
                                            :github-repos      (spec/coll-of :github/repo)
                                            :github-courses    (spec/coll-of :github/course)
                                            :courses           (spec/coll-of :offcourse/course)
                                            :raw-users         (spec/coll-of :raw/user)
                                            :raw-courses       (spec/coll-of :raw/course))))

  (defmethod action-spec :put [_]
    (spec/tuple :offcourse/actions (spec/or :bookmarks          (spec/coll-of :offcourse/bookmark)
                                            :raw-github-courses (spec/coll-of :raw/github-course)
                                            :resources          (spec/coll-of :offcourse/resource)
                                            :profiles           (spec/coll-of :offcourse/profile)
                                            :raw-portraits      (spec/coll-of :offcourse/portrait)
                                            :identities         (spec/coll-of :offcourse/identity)
                                            :errors             (spec/coll-of :offcourse/error)
                                            :nothing            nil?))))
