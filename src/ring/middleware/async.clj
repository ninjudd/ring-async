(ns ring.middleware.async
  (:require [clojure.core.async :refer [go <!]]
            [clojure.core.async.impl.channels :refer [MMC]]
            [ring.util.servlet :refer [wrap-servlet-request]])
  (:import (javax.servlet.http HttpServletRequest HttpServletResponse)
           (java.io PrintWriter)))

(defn wrap-async-body
  "Wrap a handler so that when body is a core.async channel, Servlet 3.0 Asynchronous Processing
  support will be used. This requires a handler that takes raw HttpServletRequest objects instead of
  ring response maps. You can create such a servlet using ring.util.servlet/wrap-servlet-request.
  Effectively, this means this needs to be the outermost wrapper around your handler."
  [handler]
  (with-meta
    (fn [^HttpServletRequest servlet-request]
      (let [response (handler servlet-request)]
        (if (satisfies? MMC (:body response))
          (let [chan (:body response)
                async (.startAsync servlet-request)
                ^HttpServletResponse servlet-response (.getResponse async)
                ^PrintWriter out (.getWriter servlet-response)]
            (go (loop []
                  (when-let [data (<! chan)]
                    (.write out data)
                    (.flush out)
                    (recur)))
                (.complete async))
            (dissoc response :body))
          response)))
    {:servlet-request true}))

(defn wrap-async-response 
  "A helper function to wrap a standard ring handler with support for asynchronous responses."
  [handler]
  (-> handler
      (wrap-servlet-request)
      (wrap-async-body)))
