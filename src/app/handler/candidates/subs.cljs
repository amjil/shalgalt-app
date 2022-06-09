(ns app.handler.candidates.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :candidates-list
 (fn [db _]
   (get-in db [:candidates :list])))

(re-frame/reg-sub
 :candidates-index
 (fn [db _]
   (get-in db [:candidates :index])))
