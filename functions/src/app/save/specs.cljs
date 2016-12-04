(ns app.save.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]
            [shared.models.payload.index :as payload]))

(defn specs []

  (spec/def :offcourse/payload (spec/or :bookmarks         (spec/coll-of :offcourse/bookmark)
                                      :courses           (spec/coll-of :offcourse/course)
                                      :resources         (spec/coll-of :offcourse/resource)
                                      :profiles          (spec/coll-of :offcourse/profile)
                                      :identities        (spec/coll-of :offcourse/identity)))

  (defmethod action-spec :put [_]
    (spec/tuple :offcourse/actions :offcourse/payload)))
