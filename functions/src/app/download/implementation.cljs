(ns app.download.implementation)

(defn create-portrait [[url picture] raw-portraits]
  (let [raw-portrait (->> raw-portraits first)]
    (when picture (assoc raw-portrait :portrait-data picture))))
