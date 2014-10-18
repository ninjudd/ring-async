(defproject com.ninjudd/ring-async "0.3.3"
  :description "Ring middleware adding support for asynchronous responses."
  :url "https://github.com/ninjudd/ring-async"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [ring "1.2.1"]]
  :profiles {:dev {:dependencies [[http-kit "2.1.13"]]}})
