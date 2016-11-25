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
      (let [{:keys [found errors]} (async/<! (qa/fetch github payload))]
        {:imported (when-not (empty? found) found)
         :error   (when-not (empty? errors) errors)})))

  (defmethod perform [:download :github-courses] [{:keys [github]} [_ payload]]
    (go
      (let [{:keys [found errors]} (async/<! (qa/fetch github payload))]
        {:imported (when-not (empty? found) found)
         :error   (when-not (empty? errors) errors)})))

  (defmethod perform [:download :bookmarks] [{:keys [embedly]} [_ payload]]
    (go
      (let [urls (map :resource-url payload)
            {:keys [found errors error] :as res} (async/<! (qa/fetch embedly urls))]
        {:imported (when-not (empty? found) found)
         :error   (if error error (when-not (empty? errors) errors))})))

  (defmethod perform [:download :portraits] [{:keys [http]} [_ payload]]
    (go
      (let [query {:urls (map :portrait-url payload)}
            {:keys [found errors]} (async/<! (qa/fetch http query))
            portraits (keep #(impl/create-portrait %1 payload) found)]
        {:imported (when-not (empty? portraits) portraits)
         :error    (when-not (empty? errors) errors)})))

  (defmethod perform [:download :unsupported] [_ [_ payload]]
    (go {:error :unsupported-payload}))

  (defmethod perform [:put :nothing] [_ _]
    (go {:error :no-payload}))

  (defmethod perform [:put :errors] [_ [_ errors]]
    (go errors))

  (defmethod perform :default [{:keys [bucket stage]} action]
    (ac/perform bucket (cv/to-bucket action))))
