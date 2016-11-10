(ns app.transform.implementation
  (:require [shared.models.course.index :as course]
            [shared.protocols.convertible :as cv]))

(defn extract-auth-id [{:keys [provider user_id]}]
  (str provider "|" user_id))

(defn to-identity [auth-identity user-name]
  {:auth-id (extract-auth-id auth-identity)
   :user-name user-name})

(defn to-identities [{:keys [auth-profile user-name]}]
  (map #(to-identity %1 user-name) (:identities auth-profile)))

(defn to-resource [{:keys [original_url url type title
                           description content entities
                           language keywords]}]
  (when-not (= "error" type)
    (let [record {:resource-url  original_url
                  :bookmark-url  (when (not= original_url url) original_url)
                  :resource-type type
                  :content       content
                  :language      language
                  :description   description
                  :entities      (->> entities (map :name))
                  :tags          (->> keywords (map :name))}]
      (into {} (remove (comp nil? second) record)))))

(defn to-profile [{:keys [user-name auth-profile]}]
  (-> auth-profile
      (dissoc :identities :picture)
      (assoc :user-name user-name
             :revision 1)))

(defn to-portrait [{:keys [user-name auth-profile]}]
  {:portrait-url (:picture auth-profile)
   :user-name user-name})

(defn to-bookmark [course]
  (-> course course/create cv/to-bookmarks))
