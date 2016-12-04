(ns app.download.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(defn specs []

  (spec/def :offcourse/query   (spec/or :raw-repos          (spec/coll-of :raw/repo)
                                        :raw-github-courses (spec/coll-of :raw/github-course)
                                        :bookmarks          (spec/coll-of any?)))

  (spec/def :offcourse/payload (spec/or :github-repos       (spec/coll-of :github/repo)
                                        :github-courses     (spec/coll-of :github/course)
                                        :raw-resources      (spec/coll-of :embedly/resource)
                                        :errors             (spec/coll-of :offcourse/error)
                                        :portraits          (spec/coll-of :offcourse/portrait)
                                        :nothing            nil?))

  (defmethod action-spec :download [_]
    (spec/tuple :offcourse/actions (spec/or :bookmarks      (spec/coll-of :offcourse/bookmark)
                                            :portraits      (spec/coll-of :offcourse/portrait)
                                            :raw-repos      (spec/coll-of :raw/repo)
                                            :github-courses (spec/coll-of :raw/github-course)
                                            :unsupported    any?)))

  (defmethod action-spec :put [_]
    (spec/tuple :offcourse/actions :offcourse/payload)))
