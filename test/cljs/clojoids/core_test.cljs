(ns clojoids.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [clojoids.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 2 2))))
