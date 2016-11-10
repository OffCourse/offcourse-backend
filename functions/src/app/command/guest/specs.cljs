(ns app.command.guest.specs
  (:require [cljs.spec :as spec]
            [shared.specs.raw :as raw]
            [shared.specs.action :as action :refer [action-spec]]
            [shared.specs.github :as github]
            [shared.specs.course :as course]))

(spec/def ::action-types action/types)

(defn actions []

  (defmethod action-spec :sign-in [_]
    (spec/tuple ::action-types nil?))

  (defmethod action-spec :sign-up [_]
    (spec/tuple ::action-types (spec/or :raw-user ::raw/user)))

  (defmethod action-spec :put [_]
    (spec/tuple ::action-types (spec/or :raw-users (spec/coll-of ::raw/user)))))
