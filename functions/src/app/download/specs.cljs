(ns app.download.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]
            [shared.specs.embedly :as embedly]
            [shared.specs.profile :as profile]
            [shared.specs.bookmark :as bookmark]
            [shared.specs.github :as github]))

(spec/def ::action-types action/types)

(defn actions []

  (defmethod action-spec :download [_]
    (spec/tuple ::action-types (spec/or :bookmarks  (spec/* ::bookmark/bookmark)
                                        :github-repos (spec/coll-of ::github/repo)
                                        :portraits  (spec/coll-of ::profile/portrait))))

  (defmethod action-spec :put [_]
    (spec/tuple ::action-types (spec/or :raw-resources     (spec/coll-of ::embedly/resource)
                                        :raw-portraits     (spec/coll-of ::profile/portrait)))))
