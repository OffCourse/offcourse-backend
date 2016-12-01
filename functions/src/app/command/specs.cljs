(ns app.command.specs
  (:require [cljs.spec :as spec]
            [shared.specs.action :as action :refer [action-spec]]))

(defn actions []

  (defmethod action-spec :sign-in [_]
    (spec/tuple :offcourse/actions nil?))

  (defmethod action-spec :sign-up [_]
    (spec/tuple :offcourse/actions (spec/or :raw-user :raw/user)))

  (defmethod action-spec :import [_]
    (spec/tuple :offcourse/actions (spec/or :raw-repo :raw/repo)))

  (defmethod action-spec :put [_]
    (spec/tuple :offcourse/actions (spec/or :courses (spec/coll-of :offcourse/course)
                                            :raw-users (spec/coll-of :raw/user)
                                            :raw-repos (spec/coll-of :raw/repo))))

  (defmethod action-spec :add [_]
    (spec/tuple :offcourse/actions (spec/or :course :offcourse/course))))
