(ns app.save.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]
            [shared.specs.bookmark :as bookmark]
            [shared.specs.course :as course]
            [shared.specs.resource :as resource]
            [shared.specs.profile :as profile]
            [shared.specs.identity :as identity]))

(spec/def ::action-types action/types)

(defn actions []

  (defmethod action-spec :put [_]
    (spec/tuple ::action-types (spec/or :bookmarks         (spec/coll-of ::bookmark/bookmark)
                                        :courses           (spec/coll-of ::course/course)
                                        :resources         (spec/coll-of ::resource/resource)
                                        :profiles          (spec/coll-of ::profile/profile)
                                        :identities        (spec/coll-of ::identity/identity)))))
