(ns app.core
  (:refer-clojure :exclude [filter])
  (:require [app.authorize.index :as authorize]
            [backend-shared.index]
            [app.query.index :as query]
            [app.transform.index :as transform]
            [app.command.index :as command]
            [app.filter.index :as filter]
            [app.retrieve.index :as retrieve]
            [app.index.index :as index]
            [app.debug.index :as debug]
            [app.save.index :as save]
            [app.download.index :as download]
            [cljs.nodejs :as node]))

(node/enable-util-print!)

(def ^:export command command/command)
(def ^:export authorize authorize/authorize)
(def ^:export query query/query)
(def ^:export transform transform/transform)
(def ^:export filter filter/filter)
(def ^:export download download/download)
(def ^:export save save/save)
(def ^:export index index/index)
(def ^:export debug debug/debug)
(def ^:export retrieve retrieve/retrieve)

(defn -main [] identity)
(set! *main-cli-fn* -main)
