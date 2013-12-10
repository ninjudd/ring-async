(ns ring.adapter.jetty-async
  "Asynchronous Jetty webserver configurator."
  (:import (org.eclipse.jetty.server Server Request)
           (org.eclipse.jetty.server.handler AbstractHandler)
           (javax.servlet.http HttpServletRequest HttpServletResponse))
  (:require [ring.util.servlet :as servlet]
            [ring.adapter.jetty :as jetty]
            [ring.util.async :refer [handle-async-body]]))

(defn- async-handler
  "Returns an Jetty Handler implementation supporting asynchronous responses."
  [handler]
  (proxy [AbstractHandler] []
    (handle [_ ^Request base-request servlet-request servlet-response]
      (let [request (servlet/build-request-map servlet-request)
            response (-> (handler request)
                         (handle-async-body servlet-request))]
        (when response
          (servlet/update-servlet-response servlet-response response)
          (.setHandled base-request true))))))

(defn async-configurator [configurator handler]
  (fn [^Server server]
    (.setHandler server (async-handler handler))
    (when configurator
      (configurator server))))

(defn ^Server run-jetty-async
  "Start a Jetty webserver to serve the given handler. For a list of available options, see
  ring.adapter.jetty/run-jetty. Unlike run-jetty, this server supports asynchronous responses by
  making your handler return a core.async channel for :body."
  [handler options]
  (jetty/run-jetty nil (update-in options [:configurator]
                                  async-configurator handler)))
