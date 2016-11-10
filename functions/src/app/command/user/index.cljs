(ns app.command.user.index
  (:require [app.command.user.mappings :refer [mappings]]
            [app.command.user.specs :as specs]
            [backend-shared.service.index :as service]
            [shared.protocols.actionable :as ac]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn handle [service {:keys [accepted denied error]}]
  (when denied    (service/fail service denied))
  (when accepted (service/accepted service accepted))
  (service/fail service (or error "unknown-error")))

(defn user-flow [action cb]
  (go
    (let [service (service/create :command-user cb [:bucket :stream]
                                  mappings specs/actions)]
      (handle service (<! (ac/perform service action))))))
