(ns clojoids.config)

(def dimensions [80 60])
(def game-speed 16)                                         ; ms / tick
(def turning-speed 2)                                       ; degree / tick
(def firing-rate 0.1)                                       ; shots / tick
(def asteroid-spawn-rate 180)                               ; ticks
(def max-asteroids 5)
(def bullet-ttl 3000)                                       ; ms
(def spaceship-acceleration 0.01)

(def debug?
  ^boolean goog.DEBUG)
