(ns app.handler.navigation
  (:require
    [steroid.rn.navigation.core :as navigation]
    [cljs-bean.core :as bean]))

(defn nav-reset []
  (.reset @navigation/nav-ref (bean/->js {:index 0, :routes [{:name "home"}]})))
