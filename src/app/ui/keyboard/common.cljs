(ns app.ui.keyboard.common
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [app.ui.components :as ui]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [app.ui.nativebase :as nbase]
   [app.ui.keyboard.style :refer [key-style key-con-style key-text-style]]
   [app.ui.keyboard.bridge :as bridge]

   [steroid.rn.core :as rn]))

(defn key-content [child]
  [rn/view {:style {:height "100%" :alignItems "center" :justifyContent "center"}}
   child])

(defn key-button [box-style on-press child]
  [rn/touchable-highlight {:style (merge
                                    {:backgroundColor "#f3f4f6"
                                     :flex 1
                                     :margin 4
                                     :padding 0
                                     :borderRadius 6
                                     :alignItems "center"
                                     :justifyContent "center"}
                                    box-style)
                           :underlayColor "#cccccc"
                           :onPress on-press}
    [key-content child]])

(defn key-char-button
  ([c]
   [key-button {} #(bridge/editor-insert c)
    [rn/text {:fontSize 18} c]])
  ([style c]
   [key-button {} #(bridge/editor-insert c)
    [rn/text {} c]]))


(defn key-row [child]
  (into
    [rn/view {:style {:flex 1 :flex-direction "row"
                      :alignItems "center"
                      :justifyContent "center"}}]
    child))
