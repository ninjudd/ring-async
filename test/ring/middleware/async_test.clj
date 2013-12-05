(ns ring.middleware.async-test
  (:use clojure.test)
  (:require [org.httpkit.client :as http]
            [clojure.core.async :refer [go >! <! timeout chan close!]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.async :refer [wrap-async-response]]))

(defn test-handler [request]
  (let [body (chan)]
    (go (dotimes [i 5]
          (<! (timeout 100))
          (>! body (str i "\n")))
        (close! body))
    {:body body}))

(defn get-async []
  (let [result (promise)]
    (Thread/sleep 1) ; prevent race to use all the threads before the request goes async
    (http/get "http://localhost:7700" #(deliver result (slurp (:body %))))
    result))

(deftest test-async-body
  (let [handler (wrap-async-response test-handler)
        jetty (run-jetty handler {:join? false :port 7700})]
    (doseq [result (doall
                    (repeatedly 5000 get-async))]
      (is (= "0\n1\n2\n3\n4\n" @result)))
    (.stop jetty)))
