(ns app.handler.animation
  (:require
    ["react-native" :as rn]
    [reagent.core :as r]))

(def animated (.-Animated rn))
(def animated-value (.-Value animated))
(def animated-view (r/adapt-react-class (.-View animated)))
(def animated-timing (.-timing animated))
