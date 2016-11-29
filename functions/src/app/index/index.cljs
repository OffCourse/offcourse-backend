(ns app.index.index
  (:require [app.index.mappings :refer [mappings]]
            [app.index.specs :as specs]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn initialize-service [raw-event raw-context cb]
  (service/initialize {:service-name :index
                       :callback     cb
                       :context      raw-context
                       :specs        specs/actions
                       :mappings     mappings
                       :event        raw-event
                       :adapters     [:index]}))

(defn index [& args]
  (go
    (let [{:keys [event] :as service}     (apply initialize-service args)
          {:keys [added] :as events}      (cv/to-db-events event)
          {:keys [success error] :as res} (when added (async/<! (ac/perform service [:put added])))]
      (service/done service res))))
