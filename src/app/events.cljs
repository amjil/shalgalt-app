(ns app.events
  (:require
   [re-frame.core :as re-frame]
   [app.db :as db]
   steroid.rn.navigation.events
   day8.re-frame.http-fx
   app.fx
   app.handler.candidates.events
   app.handler.keyboard.events
   app.handler.user.events))


(re-frame/reg-event-fx                                      ;; usage: (dispatch [:initialise-app])
 :initialise-app                                            ;; gets user from localstore, and puts into coeffects arg
 ;; the event handler (function) being registered
 (fn [_ _]                                                  ;; take 2 vals from coeffects. Ignore event vector itself.
   {:db               db/default-db                         ;; what it returns becomes the new application state}))
    :get-user-from-ls #(re-frame/dispatch [:set-user-from-storage %])}))

(re-frame/reg-event-fx
 :set-user-from-storage
 (fn [{db :db} [_ user]]
   (if user                                          ;; if user signed in we can get user data from ls, in that case we navigate to home
     {:db       (assoc db :user user)}
     {:db       db})))
      ; :dispatch [:navigate-to :sign-in]})))            ;; overwise open sig-in modal screen

(re-frame/reg-event-fx
  :reset-to-home
  (fn [{db :db} [_ _]]
    { :db db
      :dispatch [:navigate-to :home]
      :navigation-reset nil}))

(re-frame/reg-event-fx
  :toast
  (fn [{db :db} [_ msg]]
    {:db db
     :toast msg}))
;; -- Request Handlers -----------------------------------------------------------
;;
(re-frame/reg-event-fx
 :api-request-error                                         ;; triggered when we get request-error from the server
 (fn [{db :db} [_ request-type response]]                   ;; destructure to obtain request-type and response
   (js/console.log "api error " request-type " " (cljs-bean.core/->js response))
   (let [msg (get-in response [:response :msg])
         status (get response :status)
         msg (if (= status 500)
               "The server error."
               msg)]
     {:db (-> db                                              ;; when we complete a request we need to clean so that our ui is nice and tidy
              (assoc-in [:errors request-type] msg)
              (assoc-in [:loading request-type] false))
      :dispatch [:toast msg]})))
