(ns app.save.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(defn actions []

  (defmethod action-spec :put [_]
    (spec/tuple :offcourse/actions (spec/or :bookmarks         (spec/coll-of :offcourse/bookmark)
                                            :courses           (spec/coll-of :offcourse/course)
                                            :resources         (spec/coll-of :offcourse/resource)
                                            :profiles          (spec/coll-of :offcourse/profile)
                                            :identities        (spec/coll-of :offcourse/identity)))))
