(ns app.handler.candidates.events
  (:require
   [re-frame.core :as re-frame]
   [app.persist.sqlite :as sqlite]
   [clojure.string :as str]))

(re-frame/reg-fx
 :candidates-query
 (fn [value]
   (sqlite/candidates
    value
    #(re-frame/dispatch [:set-candidates-list %]))))

(re-frame/reg-fx
 :candidates-query-next
 (fn [value]
   (js/console.log "candidates next")
   (sqlite/next-words
    value
    #(re-frame/dispatch [:set-candidates-list %]))))

(re-frame/reg-event-fx
 :set-candidates-list
 (fn [{db :db} [_ value]]
   {:db (assoc-in db [:candidates :list] value)}))

(re-frame/reg-event-fx
 :set-candidates-index
 (fn [{db :db} [_ value]]
   {:db (assoc-in db [:candidates :index] value)
    :dispatch [:set-candidates-list []]}))

(re-frame/reg-event-fx
 :candidates-index-concat
 (fn [{db :db} [_ m]]
   (let [new-index (str (get-in db [:candidates :index]) m)]
     (js/console.log " candidates index concat " new-index)
     {:db      (assoc-in db [:candidates :index] new-index)
      :candidates-query new-index})))

;;; on delete press
(re-frame/reg-event-fx
 :keyboard-delete
 (fn [{db :db} [_ _]]
   (let [old-index (get-in db [:candidates :index])
         new-index (str/join "" (drop-last old-index))]
     (cond
       (or (empty? old-index) (= 1 (count old-index)))
       {:db       (assoc-in db [:candidates :index] "")
        :dispatch [:set-candidates-list []]}

       :else
       {:db               (assoc-in db [:candidates :index] new-index)
        :candidates-query new-index}))))

;; candidate select
(re-frame/reg-event-fx
 :candidate-select
 (fn [{db :db} [_ value]]
   {:db (-> db
            (assoc-in [:candidates :index] "")
            (assoc-in [:candidates :list] []))
    :candidates-query-next value}))

;; editor events
(re-frame/reg-event-fx
 :set-editor-content
 (fn [{db :db} [_ value]]
   {:db (assoc-in db [:editor :content] value)}))

(re-frame/reg-event-fx
 :editor-content-conj
 (fn [{db :db} [_ value]]
   (let [new-value (str (get-in db [:editor :content]) value)]
     {:db       (assoc-in db [:candidates :index] new-value)
      :dispatch [:set-editor-lines new-value]})))

(re-frame/reg-event-fx
 :set-editor-lines
 (fn [{db :db} [_ value]]
   {:db (assoc-in db [:editor :lines] value)}))

(re-frame/reg-event-fx
 :set-editor-cursor
 (fn [{db :db} [_ value]]
   {:db (assoc-in db [:editor :cursor] value)}))

(comment
  (str/join "" (drop-last "hello"))
  (re-frame/dispatch [:candidates-index-concat "ab"])
  (re-frame/dispatch [:set-candidates-index ""])
  (re-frame/dispatch [:set-candidates-list []])
  (re-frame/dispatch [:candidates-query 2])
  (re-frame/subscribe [:candidates-index])
  (re-frame/subscribe [:candidates-list])
  (re-frame/dispatch [:candidate-select {:id 665 :short_index "ab"}]))
