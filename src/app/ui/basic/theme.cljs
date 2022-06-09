(ns app.ui.basic.theme
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]

   ["@react-navigation/native" :refer [DefaultTheme DarkTheme]]
   ["react-native" :refer [Appearance]]))


; addChangeListener getColorScheme
(def theme (reagent/atom DefaultTheme))
(def current-theme (reagent/atom "light"))

(defn get-system-theme []
  (j/call Appearance :getColorScheme))

(defn is-dark? []
  (j/get @theme :dark))

(defn update-theme [m]
  (reset! current-theme m)
  (let [them (condp = m
               "dark" DarkTheme
               "light" DefaultTheme
               "system" (condp = (get-system-theme)
                          "dark" DarkTheme
                          DefaultTheme)
               DefaultTheme)]
    (reset! theme them)))


(defn color [l d]
  (if (is-dark?) d l))
