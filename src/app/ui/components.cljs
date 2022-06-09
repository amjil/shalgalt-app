(ns app.ui.components
  (:require
    [steroid.rn.core :as rn]
    [steroid.rn.components.platform :as platform]
    [steroid.rn.components.other :as other]
    ["react-native-vector-icons/Ionicons" :default ion-icons-class]
    ["react-native" :as react-native]
    [reagent.core :as reagent]
    [steroid.rn.navigation.safe-area :as safe-area]
    [clojure.string :as string]))

(def ion-icons (reagent/adapt-react-class ion-icons-class))
(def refresh-control-class (reagent/adapt-react-class react-native/RefreshControl))

(defn safe-area-consumer [& children]
  [safe-area/safe-area-consumer
   (fn [insets]
     (reagent/as-element
      (into [rn/view {:style {:flex             1 :padding-bottom (.-bottom insets)
                              :background-color :white}}]
            children)))])

(defn userpic [image size]
  (let [d size
        r (/ d 2)]
    (if (and (not (string/blank? image)) (string/starts-with? image "http"))
      [rn/image {:style  {:width d :height d :border-radius r}
                 :source {:uri image}}]
      [rn/view {:style {:width        d :height d :border-radius r
                        :border-color :gray
                        :border-width 1}}])))

(defn refresh-control [props]
  (reagent/as-element [refresh-control-class props]))
