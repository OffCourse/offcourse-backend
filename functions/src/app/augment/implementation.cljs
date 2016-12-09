(ns app.augment.implementation
  (:require [shared.protocols.loggable :as log]))

;; move this logic to course model

(defn augment-checkpoint [{:keys [resource-url] :as checkpoint} tags url]
  (if (= resource-url url)
    (assoc checkpoint :tags (take 3 tags))
    checkpoint))

(defn augment-checkpoints [course tags-data]
  (update-in course [:checkpoints]
             #(map (fn [checkpoint] (apply augment-checkpoint checkpoint tags-data)) %1)))

(defn augment-course [course tags-data]
  (reduce augment-checkpoints course tags-data))
