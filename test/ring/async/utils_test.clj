(ns ring.async.utils-test
  (:use clojure.test)
  (:require [clojure.core.async :refer [go >! <! timeout chan close! <!!]]
            [ring.util.async :refer [edn-events json-events]]
            [clojure.string :refer [split]]))

(defn slurp-events [events]
  (let [body (:body events)]
    (split (<!! (clojure.core.async/reduce str "" body))
           #"\n\n")))

(deftest test-edn-events
  (let [body (chan)
        events (edn-events body)]
    (go (dotimes [i 5]
          (>! body {:foo i}))
        (close! body))
    (is (= ["data: {:foo 0}"
            "data: {:foo 1}"
            "data: {:foo 2}"
            "data: {:foo 3}"
            "data: {:foo 4}"]
           (slurp-events events)))))

(deftest test-json-events
  (let [body (chan)
        events (json-events body)]
    (go (dotimes [i 5]
          (>! body {:foo i}))
        (close! body))
    (is (= ["data: {\"foo\":0}"
            "data: {\"foo\":1}"
            "data: {\"foo\":2}"
            "data: {\"foo\":3}"
            "data: {\"foo\":4}"]
           (slurp-events events)))))
