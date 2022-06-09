(ns app.handler.keyboard.events
  (:require
   [re-frame.core :as rf]))

;;
;; use candidate index string
;; (rf/reg-event-fx
;;   :keyboard-key-press
;;  (fn [{db}])


(defn on-press [k]
  (js/console.log "keyboard key press " k))

(defn on-delete-key []
  nil)


(comment
 (on-press "a"))
