(ns app.augment.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(spec/def ::resource-url :base/url)
(spec/def :query/bookmark (spec/keys :req-un [::resource-url]))

(defn specs []
  (spec/def :offcourse/query (spec/or :bookmarks (spec/coll-of :query/resource)
                                      :errors (spec/coll-of :offcourse/error))))
