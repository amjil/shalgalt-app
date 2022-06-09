(ns app.persist.sqlite
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [clojure.string :as str]
   [promesa.core :as p]
   [honey.sql :as hsql]
   ["react-native" :as rn]
   ["react-native-fs" :as fs]
   ["react-native-sqlite-storage" :as sqlite]))

;; (sqlite/enablePromise false)
(sqlite/enablePromise true)

(def conn (atom nil))

(defn open []
  (if-not @conn
    (let [platform    (j/get-in rn [:Platform :OS])
          file-prefix (condp = platform
                        "android" (str (as-> fs/DocumentDirectoryPath m
                                         (str/split m #"/")
                                         (drop-last m)
                                         (concat m ["databases"])
                                         (str/join "/" m)) "/")

                        "ios" (str fs/LibraryDirectoryPath "/LocalDatabase/"))]
      (.then
       (sqlite/openDatabase
        (bean/->js
         (merge
          {:name "cand"
           :createFromLocation 1})))

          ; (condp = platform
          ;   "android"
          ;   {:location "default"}
          ;   "ios" {:createFromLocation (str file-prefix "cand.db")}))))
       #(reset! conn %)))))

(defn close []
  (when @conn
    (.close @conn)
    (reset! conn nil)))

(defn is-connected? []
  (if @conn true false))

(defn rows-data [rows]
  (-> rows
      last
      (j/get :rows)
      (j/call :raw)
      (bean/->clj)))

(defn candidates [index-str return-fn]
  (cond
    (empty? index-str)
    (return-fn [])

    :else
    (let [table (first index-str)
          sql (hsql/format {:select   [:id :full_index :short_index :char_word :active_order]
                            :from     [(keyword table)]
                            :where    [:or [:= :full_index index-str]
                                       [:= :short-index index-str]]
                            :order-by [[:active_order :desc]]
                            :limit    20})]
      (p/let [result (.executeSql @conn (first sql) (bean/->js (rest sql)))]
        (p/then result
          #(do
            (if (zero? (j/get-in (first %) [:rows :length]))
              (let [sql2 (hsql/format {:select   [:id :full_index :short_index :char_word :active_order]
                                       :from     [(keyword table)]
                                       :where    [:or [:= :full_index index-str]
                                                  [:like :short-index (str index-str "%")]]
                                       :order-by [[:active_order :desc]]
                                       :limit    100})]
                (p/let [result2 (.executeSql @conn (first sql2) (bean/->js (rest sql2)))]
                  (p/then result2
                    (fn [x]
                      (return-fn (rows-data x))))))
              (return-fn (rows-data %)))))))))

(defn next-words [candidate return-fn]
  (js/console.log "next-words " (:id candidate))
  (cond
    (empty? candidate)
    []

    :else
    (let [id (:id candidate)
          table (first (:short_index candidate))
          sql (hsql/format {:select [:t2 :id2]
                            :from [:phrase1]
                            :where [:and
                                    [:= :id1 id]
                                    [:= :t1 table]]})]
      (p/let [result (.executeSql @conn (first sql) (bean/->js (rest sql)))]
        (p/then
         result
         #(let [next-data  (rows-data %)]
            (cond
              (empty? next-data)
              (return-fn [])

              :else
              (let [next-grouped (group-by (fn [x] (:t2 x)) next-data)
                    tables (keys next-grouped)
                    sqls (map (fn [[k v]]
                                {:select [:id :full_index :short_index :char_word :active_order]
                                 :from   [(keyword k)]
                                 :where  [:in :id (map (fn [x] (:id2 x)) v)]})
                              next-grouped)
                    sql (hsql/format {:select [:*]
                                      :from [{:union sqls}]
                                      :order-by [[:active_order :desc]]
                                      :limit 20})]
                ; (js/console.log "sql = " (bean/->js sql))
                (p/let [sql-result (.executeSql @conn (first sql) (bean/->js (rest sql)))]
                  (p/then sql-result
                          (fn [res]
                            (let [data (rows-data res)]
                              (return-fn data)))))))))))))
(open)
(comment
  (next-words {:id 665 :short_index "ab"} js/console.log)
  (hsql/format {:union [{:select [:*] :from [:foo]}
                        {:select [:*] :from [:bar]}]})
  sqlite/openDatabase
  open
  (open)
  (is-connected?)
  (close)
  conn
  @conn
  (.close @conn)
  (reset! conn nil)

  (.then (.executeSql @conn "select * from a where short_index = 'ab'")
         #(js/console.log "result = " (.item (.-rows (aget % 0)) 0)))

  (p/let [result (.executeSql @conn "select * from a where short_index = ?" (bean/->js ["ab"]))]
    (p/then result
            #(do (js/console.log ">>> " (.item (.-rows (aget % 0)) 0))
                 %)))

  (.then (.executeSql @conn "select * from a where short_index = ?" ["ab"])
         #(js/console.log "result = "))
  (.then (.executeSql @conn (first asql) (bean/->js (rest asql)))
        ;;  #(js/console.log "result = " (.item (.-rows (aget % 0)) 0)))
         #(js/console.log "result = "  (bean/->js (.raw (rows-data %)))))


  (.then
   (.transaction @conn
                 (fn [tx]
                   (.executeSql tx (first asql) (bean/->js (rest asql))
                                (fn [tx res]
                                  (js/console.log "result = " res)))))
   #(js/console.log "yes >>>>" %))

  (candidates "ab" js/console.log)
  (next-words {:id 665 :short_index "ab"} js/console.log)

  hsql/format
  asql
  (require '[honey.sql :as sql])
  (def asql
    (hsql/format {:select [:id :full_index :short_index]
                  :from   [:a]
                  :where  [:= :a.short_index "ab"]}))


  (range 2)
  (first "ab"))
