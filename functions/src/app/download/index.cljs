(ns app.download.index
  (:require [app.download.mappings :refer [mappings]]
            [app.download.specs :refer [specs]]
            [backend-shared.service.index :as service]
            [cljs.core.async :as async]
            [shared.protocols.actionable :as ac]
            [shared.protocols.convertible :as cv]
            [shared.protocols.specced :as sp])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def adapters {:bucket  {:bucket-names {:github-courses     (.. js/process -env -githubCoursesBucket)
                                        :portraits          (.. js/process -env -assetsBucket)
                                        :raw-resources      (.. js/process -env -resourcesBucket)
                                        :github-repos       (.. js/process -env -githubReposBucket)}}
               :http    {}
               :github  {:api-keys {:github (.. js/process -env -githubApiKey)}}
               :embedly {:api-keys {:embedly (.. js/process -env -embedlyApiKey)}}})

(defn download [& args]
  (go
    (let [{:keys [event] :as service} (apply service/create specs mappings adapters args)
          payload                     (cv/to-payload event)
          {:keys [imported error]}    (async/<! (ac/perform service [:download payload]))
          {:keys [success error]}     (async/<! (ac/perform service [:put imported]))]
      (service/done service (or success error)))))
