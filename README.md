# ring-async

Ring middleware for supporting asynchronous responses.

## Installation

To use ring-async, add the following to your `:dependencies`:

    [com.ninjudd/ring-async "0.1.0"]

This code also depends on a patch to ring, so you'll also need the following for now:

    [com.ninjudd/ring "1.2.2"]

## Usage

To return an asynchronous response that doesn't consume a single thread for the duration of the
request, simply wrap you handler in `ring.middleware.async/wrap-async-response` and return a
response map where the `:body` is a core.async channel. Then, simply use a `go` block to add data to
the body asynchronously.

```clj
(ns ring-async-sample
  (:require [clojure.core.async :refer [go >! chan close!]]
            [ring.middleware.async :refer [wrap-async-response]]))

(defn handler [request]
  (let [body (chan)]
    (go (loop [...]
          (if ...
            (>! body data)
            (recur ...)))
        (close! body))
    {:body body}))
```

## License

Copyright © 2013 Justin Balthrop

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
