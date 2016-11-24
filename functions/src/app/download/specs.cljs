(ns app.download.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(defn actions []
  (defmethod action-spec :download [_]
    (spec/tuple :offcourse/actions (spec/or :bookmarks      (spec/coll-of :offcourse/bookmark)
                                            :portraits      (spec/coll-of :offcourse/portrait)
                                            :github-repos   (spec/coll-of :github/repo)
                                            :github-courses (spec/coll-of :github/course))))

  (defmethod action-spec :put [_]
    (spec/tuple :offcourse/actions (spec/or :raw-resources      (spec/coll-of :embedly/resource)
                                            :github-repos       (spec/coll-of :github/repo)
                                            :github-courses     (spec/coll-of :github/course)
                                            :raw-portraits      (spec/coll-of :raw/portrait)))))
