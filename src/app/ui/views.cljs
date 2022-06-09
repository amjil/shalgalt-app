(ns app.ui.views
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [steroid.rn.core :as rn]
   [steroid.rn.navigation.core :as rnn]
   [steroid.rn.navigation.bottom-tabs :as bottom-tabs]
   [steroid.rn.components.status-bar :as status-bar]
   [steroid.rn.navigation.safe-area :as safe-area]
   [steroid.rn.components.platform :as platform]
   [steroid.rn.components.ui :as rn-ui]
   [app.ui.components :as ui]
   [steroid.rn.navigation.stack :as stack]
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [app.handler.gesture :as gesture]

   ["react-native-smooth-blink-view" :default blinkview]
   ["react-native-svg" :as svg]
   ["react-native-linear-gradient" :default linear-gradient]
   ["@react-navigation/stack" :refer [createStackNavigator]]
   ["react-native-portalize" :refer [Host]]
   ["native-base" :refer [extendTheme]]

   [app.ui.home.views :as home]
   [app.ui.drawer.index :as drawer]
   [app.ui.nativebase :as nativebase]
   [app.ui.editor :as editor]
   [app.ui.basic.theme :as theme]
   [app.ui.search :as search]
   [app.ui.exam.views :as exam]
   [app.ui.profile.views :as profile]))


(when platform/android?
  (status-bar/set-bar-style "light-content")
  (status-bar/set-translucent true))


(def tab-icons
  {"home"         "md-home"
   "exam"         "md-reader"
   ; "notice"       "md-notifications"
   "profile"      "md-person"})

(defn screen-options [options]
  (let [{:keys [route]} (bean/->clj options)]
    {:tabBarIcon
     (fn [data]
       (let [{:keys [color]} (bean/->clj data)
             icon (get tab-icons (:name route))]
         (reagent/as-element
          [ui/ion-icons {:name icon :color color :size 30}])))}))

(defn home []
  (let [h (reagent/atom nil)]
    (fn []
      [rn/view {:style {:height "100%" :width "100%"}
                :on-layout #(let [height (j/get-in % [:nativeEvent :layout :height])]
                              (reset! h height))}])))

(defn edit-view []
  [nativebase/center {:flex 1 :px 3 :safeArea true}
   [nativebase/text "edit-view"]])

(defn tabs []
  [bottom-tabs/bottom-tab
   {
    ; :screenOptions screen-options}
    :screenOptions
    (fn [options]
     (let [{:keys [route]} (bean/->clj options)
           icon (get tab-icons (:name route))]
       (bean/->js {:activeTintColor   "#5cb85c"
                   :inactiveTintColor :black
                   :showLabel         false
                   :tabBarLabel       (fn [] nil)
                   ; :headerShown       false
                   :modal             true
                   :tabBarIcon (fn [data]
                                 (let [{:keys [color]} (bean/->clj data)
                                       icon (get tab-icons (:name route))]
                                   (reagent/as-element
                                     [ui/ion-icons {:name icon :color color :size 20}])))})))}

   [{:name      :home
     :component home/home}
    {:name      :exam
     :component exam/view}
    ; (merge
    ;   home/home
    ;   {:name :notice})
    {:name      :profile
     :component profile/profile}]])

(defn create-stack-navigator []
  (let [^js stack (createStackNavigator)]
    [(reagent/adapt-react-class (.-Navigator stack))
     (reagent/adapt-react-class (.-Group stack))
     (reagent/adapt-react-class (.-Screen stack))]))

(defn prepare-navigator [navigator screen]
  (fn [& params]
    (let [[props children] (if (map? (first params))
                             [(first params) (second params)]
                             [{} (first params)])]
      (into [navigator props]
            (mapv (fn [props]
                    [screen (update props :component reagent/reactify-component)])
                  children)))))

(defn root-stack []
  (let [[navigator group screen] (create-stack-navigator)]
    [safe-area/safe-area-provider
     [(rnn/create-navigation-container-reload                 ;; navigation container with shadow-cljs hot reload
       {:on-ready #(re-frame/dispatch [:initialise-app])     ;; when navigation initialized and mounted initialize the app
        :theme @theme/theme}
       [nativebase/nativebase-provider {:config {:dependencies {"linear-gradient" linear-gradient}}}
        [gesture/gesture-root-view {:style {:flex 1}}
         [:> Host
          [navigator {:screenOptions {:headerShown false}}
           (into
             [group {}]
             (mapv (fn [props]
                     [screen (update props :component reagent/reactify-component)])
               [{:name      :main
                 :component tabs
                 :options {:title ""}}
                profile/profile-edit]))
           (into
             [group {:screenOptions {:presentation "modal"}}]
             (mapv (fn [props]
                     [screen (update props :component reagent/reactify-component)])
               [search/search-base]))]]]])]]))
