(ns app.index.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(defn actions []
  (defmethod action-spec :put [_]
    (spec/tuple :offcourse/actions (spec/or :resources (spec/coll-of :offcourse/resource)
                                            :profiles  (spec/coll-of :offcourse/profile)
                                            :courses   (spec/coll-of :offcourse/course)))))
