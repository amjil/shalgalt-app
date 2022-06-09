(ns app.fx
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [cljs-bean.core :as bean]
   [applied-science.js-interop :as j]
   [steroid.rn.components.async-storage :as async-storage]
   [app.ui.nativebase :as nbase]
   [app.handler.navigation :as navigation]
   ["react-native-measure-text-chars" :as rntext]
   ["native-base" :refer [useToast useStyledSystemPropsResolver Toast]]))

;; copied from https://github.com/flexsurfer/conduitrn


;; ALL SIDE EFFECTS ARE HERE

;; -- Async Storage  ----------------------------------------------------------
;;
;; Part of the conduit challenge is to store a user in Async Storage, and
;; on app startup, reload the user from when the program was last run.
;;
(def app-user-key "app-user")  ;;  key

(re-frame/reg-fx
 :store-user-in-ls
 (fn [user]
   (async-storage/set-item app-user-key user)))

(re-frame/reg-fx
 :remove-user-from-ls
 (fn []
   (async-storage/remove-item app-user-key)))

(re-frame/reg-fx
 :get-user-from-ls
 (fn [cb]
   (async-storage/get-item app-user-key cb)))

(re-frame/reg-fx
 :navigation-reset
 (fn []
   (navigation/nav-reset)))
