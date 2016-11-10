(ns app.index.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]
            [shared.specs.resource :as resource]
            [shared.specs.course :as course]
            [shared.specs.profile :as profile]))

(spec/def ::action-types action/types)

(defn actions []

  (defmethod action-spec :put [_]
    (spec/tuple ::action-types (spec/or :resources (spec/coll-of ::resource/resource)
                                        :profiles (spec/coll-of ::profile/profile)
                                        :courses (spec/coll-of ::course/course)))))
