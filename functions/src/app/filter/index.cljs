(ns app.filter.index
  (:refer-clojure :exclude [filter])
  (:require [cljs.nodejs :as node]
            [shared.protocols.loggable :as log]
            [shared.protocols.queryable :as qa]
            [shared.protocols.specced :as sp]
            [shared.protocols.convertible :as cv :refer [Convertible]]
            [backend-shared.index]
            [backend-shared.service.index :as service]
            [app.filter.mappings :refer [mappings]]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [backend-shared.aws-event.index :as aws-event]
            [shared.specs.action :as as])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn filter [raw-event context cb]
  (log/log raw-event)
  (go
    (let [service (service/create :filter cb [:db :stream] mappings identity)
          payload (-> raw-event cv/to-payload)
          res     (async/<! (ac/perform service [:filter payload]))]
      (when res
        (ac/perform service [:put res]))
      (service/done service res))))
