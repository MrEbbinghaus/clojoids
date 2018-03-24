(ns clojoids.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [clojoids.core-test]))

(doo-tests 'clojoids.core-test)
