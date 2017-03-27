(set-env!
 :resource-paths #{"src"}
 :dependencies  '[[adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                  [adzerk/boot-cljs-repl       "0.3.3"          :scope "test"]
                  [adzerk/boot-reload          "0.4.12"         :scope "test"]
                  [pandeiro/boot-http          "0.7.3" :scope "test"]
                  [crisptrutski/boot-cljs-test "0.3.0-SNAPSHOT" :scope "test"]
                  [boot-codox                  "0.10.0" :scope "test"]
                  [org.clojure/clojure         "1.9.0-alpha14"]
                  [org.clojure/core.async      "0.2.391"]
                  [org.clojure/test.check      "0.9.0"]
                  [org.clojure/clojurescript   "1.9.229"]
                  [com.cemerick/piggieback     "0.2.2-SNAPSHOT"          :scope "test"]
                  [offcourse/shared            "0.5.7"]
                  [offcourse/backend-shared    "0.2.0"]
                  [weasel                      "0.7.0"          :scope "test"]
                  [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[crisptrutski.boot-cljs-test  :refer [test-cljs]]
 '[codox.boot :refer [codox]]
 '[pandeiro.boot-http    :refer [serve]])

(deftask testing []
  (merge-env! :resource-paths #{"test"})
  identity)

(deftask auto-test []
  (comp (testing)
        (watch)
        (speak)
        (test-cljs)))

(deftask build []
  (task-options! cljs   {:compiler-options {:optimizations :simple
                                            :target :nodejs}})
  (comp (cljs)
        (target)))

(deftask test []
  (comp (testing)
        (test-cljs)))

(deftask dev []
  (comp (watch)
        (checkout)
        (speak)
        (cljs-repl)
        (build)))
