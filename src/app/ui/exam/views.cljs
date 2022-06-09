(ns app.ui.exam.views
  (:require
   [reagent.core :as reagent]
   [app.ui.components :as ui]
   [app.ui.text :as text]
   [app.ui.nativebase :as nbase]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   [steroid.rn.core :as rn]
   [steroid.rn.components.list :as rnlist]
   [steroid.rn.components.touchable :as touchable]

   [app.handler.animatable :as animatable]

   [app.ui.basic.theme :as theme]
   ["react-native" :refer [Dimensions Appearance]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]))

(defn view []
  [nbase/center {:flex 1 :safeArea true}
   [nbase/text "exam view"]])
