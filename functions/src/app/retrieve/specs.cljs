(ns app.retrieve.specs
  (:require [shared.specs.action :as action :refer [action-spec]]
            [cljs.spec :as spec]))

(defn actions []

  (spec/def :offcourse/query   (spec/or :bucket-items   (spec/coll-of :aws/bucket-item-query)))

  (spec/def :offcourse/payload (spec/or :courses        (spec/coll-of :raw/course)
                                        :raw-resources  (spec/coll-of :embedly/resource)
                                        :github-courses (spec/coll-of :github/course)
                                        :github-repos   (spec/coll-of :github/repo)
                                        :raw-users      (spec/coll-of :raw/user)
                                        :errors         (spec/coll-of :offcourse/error)
                                        :nothing        nil?))

  (defmethod action-spec :put [_]
    (spec/tuple :offcourse/actions :offcourse/payload)))
