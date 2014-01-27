(ns ring.util.async
  (:require [clojure.core.async :refer [go <! map< close!]]
            [clojure.core.async.impl.protocols :refer [Channel]]
            [cheshire.core :as json])
  (:import (javax.servlet.http HttpServletRequest HttpServletResponse)
           (java.io PrintWriter)))

(defn handle-async-body [response ^HttpServletRequest servlet-request]
  (if (satisfies? Channel (:body response))
    (let [chan (:body response)
          async (.startAsync servlet-request)
          ^HttpServletResponse servlet-response (.getResponse async)
          content-type (get-in response [:headers "Content-Type"])]
      (.setContentType servlet-response content-type)
      (let [^PrintWriter out (.getWriter servlet-response)]
        (go (loop []
              (when-let [data (<! chan)]
                (.write out data)
                (.flush out)
                (recur)))
            (close! chan)
            (.complete async)))
      (dissoc response :body))
    response))

(defn add-sse-headers [request]
  (-> request
      (assoc-in [:headers "Content-Type"] "text/event-stream; charset=utf-8")
      (assoc-in [:headers "Cache-Control"] "no-cache")))

(defn edn-events [chan]
  (add-sse-headers
   {:body (map< (fn [event]
                  (str "data: "
                       (pr-str event)
                       "\n\n"))
                chan)}))

(defn json-events [chan]
  (add-sse-headers
   {:body (map< (fn [event]
                  (str "data: "
                       (json/generate-string event)
                       "\n\n"))
                chan)}))
