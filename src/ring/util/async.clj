(ns ring.util.async
  (:require [clojure.core.async :refer [go <!]]
            [clojure.core.async.impl.protocols :refer [Channel]])
  (:import (javax.servlet.http HttpServletRequest HttpServletResponse)
           (java.io PrintWriter)))

(defn handle-async-body [response ^HttpServletRequest servlet-request]
  (if (satisfies? Channel (:body response))
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
    response))
