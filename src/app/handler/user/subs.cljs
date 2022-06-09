(ns app.handler.user.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :user-token
 (fn [db _]
   (get-in db [:user :token])))
