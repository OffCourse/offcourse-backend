(ns app.query.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(spec/def ::action-types action/types)

(defn actions [])
