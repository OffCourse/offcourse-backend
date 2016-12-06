(ns app.augment.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(spec/def :query/course-by-id (spec/keys :req-un [:course/course-id :course/revision]))

(defn specs []
  (spec/def :offcourse/query (spec/or :bookmarks (spec/coll-of :query/resource)
                                      :courses (spec/coll-of :query/course-by-id)
                                      :errors (spec/coll-of :offcourse/error))))
