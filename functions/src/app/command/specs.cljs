(ns app.command.specs
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

  (defmethod action-spec :import [_]
    (spec/tuple ::action-types (spec/or :github-repo ::github/repo)))

  (defmethod action-spec :add [_]
    (spec/tuple ::action-types (spec/or :course ::course/course))))
