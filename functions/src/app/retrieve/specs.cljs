(ns app.retrieve.specs
  (:require [shared.specs.action :as action :refer [action-spec]]
            [cljs.spec :as spec]
            [shared.specs.raw :as raw]
            [shared.specs.github :as github]
            [shared.specs.embedly :as embedly]))


(spec/def ::action-types action/types)

(defn actions []
  (defmethod action-spec :put [_]
    (spec/tuple ::action-types (spec/or :courses (spec/coll-of ::raw/course)
                                        :raw-resources (spec/coll-of ::embedly/resource)
                                        :github-courses (spec/coll-of ::github/course)
                                        :github-repos (spec/coll-of ::github/repo)
                                        :raw-users (spec/coll-of ::raw/user)))))
