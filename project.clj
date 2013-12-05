(defproject com.ninjudd/ring-async "0.1.0"
  :description "Ring middleware adding support for asynchronous responses."
  :url "https://github.com/ninjudd/ring-async"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]]
  :profiles {:dev {:dependencies [[ring/ring "1.2.1"]
                                  [http-kit "2.1.13"]]}})
