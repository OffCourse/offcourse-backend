(ns app.index.index
  (:require [app.index.mappings :refer [mappings]]
            [app.index.specs :refer [specs]]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def adapters {:index {:search-url (.. js/process -env -elasticsearchEndpoint)}})

(defn index [& args]
  (go
    (let [{:keys [event] :as service}     (apply service/create specs mappings adapters args)
          {:keys [added] :as events}      (cv/to-db-events event)
          {:keys [success error] :as res} (when added (async/<! (ac/perform service [:put added])))]
      (service/done service res))))
