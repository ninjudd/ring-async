(ns ring.util.async
  (:require [clojure.core.async :refer [go <! close!]]
            [clojure.core.async.impl.protocols :refer [Channel]])
  (:import (javax.servlet.http HttpServletRequest HttpServletResponse)
           (javax.servlet ServletOutputStream)))

(defn handle-async-body [response ^HttpServletRequest servlet-request]
  (if (satisfies? Channel (:body response))
    (let [chan (:body response)
          async (doto (.startAsync servlet-request)
                  (.setTimeout 0))
          ^HttpServletResponse servlet-response (.getResponse async)
          content-type (get-in response [:headers "Content-Type"])]
      (.setContentType servlet-response content-type)
      (let [^ServletOutputStream out (.getOutputStream servlet-response)]
        (go (loop []
              (when-let [data (<! chan)]
                (try (if (= :flush data)
                       (.flushBuffer servlet-response)
                       (do (.write out data)
                           (.flush out)))
                     (catch java.io.IOException e
                       (close! chan)))
                (recur)))
            (.complete async)))
      (dissoc response :body))
    response))
