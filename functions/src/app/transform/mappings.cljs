(ns app.transform.mappings
  (:require [backend-shared.service.index :refer [perform]]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [app.transform.implementation :as impl]))

(defn mappings []

  (defmethod perform [:transform :raw-users] [_ [_ payload]]
    {:profiles (map impl/to-profile payload)
     :portraits (map impl/to-portrait payload)
     :identities (mapcat impl/to-identities payload)})

  (defmethod perform [:transform :embedly] [_ [_ raw-resources]]
    (let [converted (keep impl/to-resource raw-resources)]
      (if-not (empty? converted)
        {:resources converted}
        {:error raw-resources})))

  (defmethod perform [:transform :courses] [_ [_ courses]]
    {:bookmarks (mapcat impl/to-bookmark courses)})

  (defmethod perform :default [{:keys [stream stage]} action]
    (ac/perform stream (cv/to-stream action))))
