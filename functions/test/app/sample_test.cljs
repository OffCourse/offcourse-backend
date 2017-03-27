(ns app.sample-test
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop :include-macros true]
            [cljs.spec :as s]
            [cljs.spec.test :as stest]
            [cljs.test :refer-macros [deftest is]]
            [clojure.pprint :as pprint]))

(def sort-idempotent-prop
  (prop/for-all [v (gen/vector gen/int)]
    (= (sort v) (sort (sort v)))))

(tc/quick-check 100 sort-idempotent-prop)

;; Sample function and a function spec
(defn fooo [i] (+ i 20))

(s/fdef fooo
  :args (s/cat :i integer?)
  :ret integer?
  :fn #(> (:ret %) (-> % :args :i)))

;; Utility functions to intergrate clojure.spec.test/check with clojure.test
(defn summarize-results' [spec-check]
  (map (comp #(pprint/write % :stream nil) stest/abbrev-result) spec-check))

(defn check' [spec-check]
  (is (nil? (-> spec-check first :failure)) (summarize-results' spec-check)))

;; Tests
(deftest fooish (check' (stest/check `fooo)))
