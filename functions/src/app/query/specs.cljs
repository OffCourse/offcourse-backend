(ns app.query.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :refer [action-spec]]))

(defn specs []
  (spec/def :offcourse/query (spec/or :course             :query/course
                                      :collection         :query/collection
                                      :resource           :query/resource)))
