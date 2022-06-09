(ns app.ui.drawer.index
  (:require
   ["@react-navigation/drawer" :refer [createDrawerNavigator
                                       DrawerContentScrollView]]
   [reagent.core :as reagent]
   [app.ui.components :as ui]
   [app.ui.home.views :as home]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   [steroid.rn.core :as rn]
   [steroid.rn.navigation.core :as rnn]
   [steroid.rn.components.list :as rnlist]
   [steroid.rn.components.touchable :as touchable]
   [steroid.rn.navigation.safe-area :as safe-area]
   [steroid.rn.navigation.core :as rnnav]
   [steroid.rn.utils :as utils]
   [promesa.core :as p]))

(defn create-drawer-navigator-screen []
  (let [^js drawer (createDrawerNavigator)]
    [(reagent/adapt-react-class (.-Navigator drawer))
     (reagent/adapt-react-class (.-Screen drawer))]))

(defn create-drawer-navigator []
  (let [^js drawer (createDrawerNavigator)]
    (reagent/adapt-react-class (.-Navigator drawer))))

(def drawer-content-scroll-view (reagent/adapt-react-class DrawerContentScrollView))

(defn drawer []
  (let [[navigator screen] (create-drawer-navigator-screen)]
    (utils/prepare-navigator navigator screen)))


(defn view []
  [drawer {:screenOptions
           {
            :drawerPosition :left
            ; :swipeEdgeWidth 10
            :drawerStyle {:width 300}
            ; :overlayColor "00FFFFF"
            :drawerType :front}
            ; :overlayStyle {:opacity 0}};:backgroundColor :whitesmoke}}
            ; :header (fn [navigation route options] nil)
            ; :headerShown false}
           :drawerContent
           (fn [props]
             (reagent/as-element
               ; [drawer-content-scroll-view {}
                ; [safe-area/safe-area-view
                 [rnn/navigation-container {:independent true}
                  [drawer {:screenOptions
                           {
                            :drawerPosition :left
                            ; :swipeEdgeWidth 10
                            ; :overlayColor :transparent
                            ; :overlayStyle {:opacity 0 :backgroundColor "transparent"}
                            ; :overlayColor "00FFFFF"
                            :drawerType :front
                            :drawerStyle {:width 200}}
                                          ; :backgroundColor "transparent"}
                            ; :headerShown false}
                           :drawerContent
                           (fn [props]
                             (reagent/as-element
                               [drawer-content-scroll-view {}
                                [ui/safe-area-consumer
                                 [rn/text "hello Hi"]]]))}
                   [{:name :front
                     :component
                     (fn []
                       [safe-area/safe-area-view
                        [rn/text "hello world!"]])}]]]))}

    [{:name :main
      :component
      ; home/home}]])
      (fn []
        [ui/safe-area-consumer
         [rn/text "hello world!"]])}]])
