(ns app.download.index
  (:require [app.download.mappings :refer [mappings]]
            [app.download.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn initialize-service [raw-event raw-context cb]
  (service/initialize {:service-name :download
                       :callback     cb
                       :context      raw-context
                       :specs        specs/actions
                       :mappings     mappings
                       :event        raw-event
                       :adapters     [:bucket :http :github :embedly]}))

(defn download [& args]
  (go
    (let [{:keys [event] :as service} (apply initialize-service args)
          payload                     (cv/to-payload event)
          {:keys [imported error]}    (async/<! (ac/perform service [:download payload]))
          {:keys [success error]}      (async/<! (ac/perform service [:put imported]))]
      (service/done service (or success error)))))
