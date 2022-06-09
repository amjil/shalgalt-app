(ns app.handler.animatable
  (:require
    [reagent.core :as r]
    [steroid.rn.core :as rn]
    ["react-native-animatable" :as Animatable]))

(def text (r/adapt-react-class (.-Text Animatable)))
(def view (r/adapt-react-class (.-View Animatable)))
(def image (r/adapt-react-class (.-Image Animatable)))
