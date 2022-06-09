(ns app.core
  (:require
   [steroid.rn.core :as rn]
   [app.ui.views :as views]
   [app.ui.nativebase :as nbase]
   [honey.sql :as hsql]
   ["react-native-sqlite-storage" :as sqlite]
   ["react-native-measure-text-chars" :refer [measure]]
   app.events
   app.subs))

(defn root-stack []
  [nbase/center {:flex 1 :safeArea true}
   [nbase/text "hello world"]])

(defn init []

  (rn/register-comp "shalgalt" views/root-stack))
