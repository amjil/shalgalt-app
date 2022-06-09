(ns app.ui.keyboard.bridge
  (:require
    [app.ui.editor :refer [webref cursor weblen]]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]))

(defn editor-insert [x]
  (j/call @webref :postMessage
    (j/call js/JSON :stringify
      (bean/->js {:type "insertText" :message {:index (:index @cursor)
                                               :text x}}))))

(defn editor-delete []
  (j/call @webref :postMessage
    (j/call js/JSON :stringify
      (bean/->js {:type "deleteText"
                  :message {:start (dec (:index @cursor))
                            :end (:index @cursor)}}))))

(defn editor-content []
  (if @webref
    (j/call @webref :postMessage
      (j/call js/JSON :stringify
        (clj->js {:type "getContent"
                  :message ""})))))

(defn editor-empty? []
  (= 0 @weblen))
