(ns app.download.mappings
  (:require [backend-shared.service.index :refer [perform]]
            [app.download.implementation :as impl]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.queryable :as qa]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn mappings []
  (defmethod perform [:download :github-repos] [{:keys [github]} [_ payload]]
    (go
      (let [{:keys [found] :as res} (async/<! (qa/fetch github payload))]
        res)))


  (defmethod perform [:download :bookmarks] [{:keys [embedly]} [_ payload]]
    (go
      (let [urls (map :resource-url payload)
            {:keys [found errors] :as res} (async/<! (qa/fetch embedly urls))]
        {:imported found :errors errors})))

  (defmethod perform [:download :portraits] [{:keys [http]} [_ payload]]
    (go
      (let [query {:urls (map :portrait-url payload)}
            {:keys [found errors] :as res} (async/<! (qa/fetch http query))
            portraits (keep #(impl/create-portrait %1 payload) found)]
        {:imported portraits :errors errors})))

  (defmethod perform [:put :raw-resources] [{:keys [bucket]} action]
    (ac/perform bucket (cv/to-bucket action)))

  (defmethod perform [:put :raw-portraits] [{:keys [bucket stage]} action]
    (ac/perform bucket (cv/to-bucket action))))
