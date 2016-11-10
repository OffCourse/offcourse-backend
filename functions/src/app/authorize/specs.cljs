(ns app.authorize.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]
            [shared.specs.aws :as aws]))

(spec/def ::action-types action/types)

(defn actions []

  (defmethod action-spec :verify [_]
    (spec/tuple ::action-types (spec/or :credentials ::aws/auth-event)))

  (defmethod action-spec :create [_]
    (spec/tuple ::action-types (spec/or :credentials ::aws/auth-event))))
