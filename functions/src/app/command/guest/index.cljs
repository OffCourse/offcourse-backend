(ns app.command.guest.index
  (:require [app.command.guest.mappings :refer [mappings]]
            [app.command.guest.specs :as specs]
            [backend-shared.service.index :as service]
            [shared.protocols.actionable :as ac])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn handle [service {:keys [accepted denied error]}]
  (when denied    (service/unauthorized service denied))
  (when accepted (service/accepted service accepted))
  (service/fail service (or error "unknown-error")))

(defn guest-flow [action cb]
  (go
    (let [service (service/create :command-guest cb [:bucket] mappings specs/actions)]
      (handle service (<! (ac/perform service action))))))
