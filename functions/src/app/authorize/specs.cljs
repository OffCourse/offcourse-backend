(ns app.authorize.specs
  (:require [cljs.spec :as spec]
            [backend-shared.specs.index]
            [shared.specs.action :refer [action-spec]]))

(defn actions []

  (defmethod action-spec :verify [_]
    (spec/tuple :offcourse/actions (spec/or :credentials :aws/credentials)))

  (defmethod action-spec :create [_]
    (spec/tuple :offcourse/actions (spec/or :credentials :aws/credentials))))
