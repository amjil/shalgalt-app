(ns app.handler.gesture
  (:require
    [reagent.core :as r]
    [steroid.rn.core :as rn]
    ["react-native-gesture-handler" :refer [PanGestureHandler
                                            TapGestureHandler
                                            FlingGestureHandler
                                            LongPressGestureHandler
                                            State
                                            ScrollView
                                            GestureHandlerRootView]]
    [applied-science.js-interop :as j]))

(def pan-gesture-handler (r/adapt-react-class PanGestureHandler))

(def tap-gesture-handler (r/adapt-react-class TapGestureHandler))

(def fling-gesture-handler (r/adapt-react-class FlingGestureHandler))

(def long-press-gesture-handler (r/adapt-react-class LongPressGestureHandler))

(def gesture-root-view (r/adapt-react-class GestureHandlerRootView))

(def scroll-view (r/adapt-react-class ScrollView))

(def state State)

(defn long-press-active [evt]
  (=  (j/get state :ACTIVE)
      (j/get evt :state)))

(defn tap-state-end [evt]
  (=  (j/get state :END)
      (j/get evt :state)))

(defn pan-handler-active [evt]
  (=  (j/get state :ACTIVE)
      (j/get evt :state)))
