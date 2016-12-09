(ns app.augment.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(spec/def :query/course-by-id (spec/keys :req-un [:course/course-id :course/revision]))

(spec/def ::courses (spec/coll-of :offcourse/course))
(spec/def ::resources (spec/coll-of :offcourse/resource))
(spec/def ::motherload (spec/keys :req-un [::courses ::resources]))

(defn specs []

  (spec/def :offcourse/query (spec/or :bookmarks (spec/coll-of :offcourse/resource)
                                      :resources (spec/coll-of :offcourse/bookmark)
                                      :courses (spec/coll-of :query/course-by-id)
                                      :errors (spec/coll-of :offcourse/error)))

  (defmethod action-spec :transform [_]
    (spec/tuple :offcourse/actions (spec/or :motherload ::motherload)))

  (spec/def :offcourse/payload (spec/or :courses ::courses
                                        :error   (spec/coll-of :offcourse/error)
                                        :nothing nil?))

  (defmethod action-spec :put [_]
    (spec/tuple :offcourse/actions :offcourse/payload)))
