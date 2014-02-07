# ring-async

Ring adapter for supporting asynchronous responses.

## Installation

To use ring-async, add the following to your `:dependencies`:

    [com.ninjudd/ring-async "0.2.0"]

## Usage

To return an asynchronous response that doesn't consume a thread for the duration of the request,
just use `ring.adapter.jetty-async/run-jetty-async` instead of `ring.adapter.jetty/run-jetty` and
return a response map where the `:body` is a core.async channel. Then, simply use a `go` block to
add data to the body asynchronously.

```clj
(ns ring-async-sample
  (:require [clojure.core.async :refer [go >! chan close!]]
            [ring.adapter.jetty-async :refer [run-jetty-async]]))

(defn handler [request]
  (let [body (chan)]
    (go (loop [...]
          (if ...
            (>! body data)
            (recur ...)))
        (close! body))
    {:body body}))

(defn start []
  (run-jetty-async handler {:join? false :port 8000}))
```

## Server-Sent Events

If you'd like to use ring-async for Server-Sent Events, you can use [eventual](http://github.com/ninjudd/eventual), which provides helper libraries for both the Server in Clojure and the Client in ClojureScript.

## License

Copyright © 2013 Justin Balthrop

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
