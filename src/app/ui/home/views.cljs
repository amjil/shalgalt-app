(ns app.ui.home.views
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
   ["react-native-modal" :default rnmodal]
   ["react-native" :refer [Dimensions Appearance]]
   ["react-native-vector-icons/Ionicons" :default Ionicons]))

; (defn home []
;   ; (let [current-theme (reagent/atom (if (true? (j/get @theme/theme :dark))
;   ;                                     "dark"
;   ;                                     "light"))]
;     ; (fn []
;       [nbase/center {:flex 1 :safeArea true}
;        [nbase/button { :colorScheme (theme/color "teal" "blue")
;                        :onPress (fn [e]
;                                   (js/console.log "theme >>> "))}
;                                   ; (let [th (condp = @current-theme
;                                   ;            "light" "dark"
;                                   ;            "dark" "light")]
;                                   ;   (reset! current-theme th)
;                                   ;   (theme/update-theme th)))}
;          "update theme"]])

(defn home []
  [nbase/center {:flex 1 :safeArea true}
   [nbase/text "hello world"]])
