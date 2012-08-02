(ns stencil.test.cache
  (:refer-clojure :exclude [load])
  (:use clojure.test
        stencil.loader)
  (:require [clojure.core.cache :as cache]))

(deftest test-ttl-cache
  "Set up a ttl cache with a 10-second TTL and try loading two templates."
  (let [template "templates/sample"
        x        2000]

    (is (not (nil? (find-file template))))

    ; @TODO: DRY.
    ; This run will be done with the default LRUCache.
    (is (= nil (cache-get template)) "The first invocation of cache get should fail.")
    (load template)
    (Thread/sleep (/ 2 x))
    (is (not (= nil (cache-get template))) "Immediately after loading a template, a cache get should succeed.")
    (Thread/sleep (* 2 x))
    (is (not (= nil (cache-get template))) "After double the TTL, the LRU cache should still be full.")


    ; @TODO: DRY.
    (set-cache (cache/ttl-cache-factory {} :ttl x))
    (is (= nil (cache-get template)) "The first invocation of cache get should fail.")
    (load template)
    (Thread/sleep (/ 2 x))
    (is (not (= nil (cache-get template))) "Immediately after loading a template, a cache get should succeed.")
    (Thread/sleep (* 2 x))
    (is (= nil (cache-get template)) "After double the TTL, the TTL cache should not be full.")))
