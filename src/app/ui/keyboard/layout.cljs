(ns app.ui.keyboard.layout
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [app.ui.components :as ui]
   [app.ui.keyboard.candidates :as candidates]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [clojure.string :as str]
   [app.ui.nativebase :as nbase]
   [app.ui.keyboard.style :as style :refer [key-style key-con-style key-text-style]]
   [app.ui.keyboard.common :as keycommon]
   [app.ui.keyboard.state :as state]
   [app.ui.keyboard.bridge :as bridge]
   [app.ui.text :as text]

   [steroid.rn.core :as rn]))

(def mn-key-list [[{:label "ᠣ" :code "q"} {:label "ᠸ᠊" :code "w"} {:label "ᠡ" :code "e"}
                   {:label "ᠷ᠊" :code "r"} {:label "ᠲ᠊" :code "t"} {:label "ᠶ᠊" :code "y"}
                   {:label "ᠦ᠊" :code "u"} {:label "ᠢ" :code "i"} {:label "ᠥ" :code "o"} {:label "ᠫ᠊" :code "p"}]
                  [{:label "ᠠ" :code "a"} {:label "ᠰ᠊" :code "s"} {:label "ᠳ" :code "d"} {:label "ᠹ᠊" :code "f"}
                   {:label "ᠭ᠊" :code "g"} {:label "ᠬ᠊" :code "h"} {:label "ᠵ᠊" :code "j"}
                   {:label "ᠺ᠊" :code "k"} {:label "ᠯ᠊" :code "l"} {:label " ᠩ" :code "ng"}]
                  [{:label "ᠽ᠊" :code "z"} {:label "ᠱ᠊" :code "x"} {:label "ᠴ᠊" :code "c"}
                   {:label "ᠤ᠊" :code "v"} {:label "ᠪ᠊" :code "b"}
                   {:label "ᠨ᠊" :code "n"} {:label "ᠮ᠊" :code "m"}]])

(def en-key-list
  [["q" "w" "e" "r" "t" "y" "u" "i" "o" "p"]
   ["a" "s" "d" "f" "g" "h" "j" "k" "l"]
   ["z" "x" "c" "v" "b" "n" "m"]])

(def en-key-list-n
  [["1" "2" "3" "4" "5" "6" "7" "8" "9" "0"]
   ["[" "]" "{" "}" "#" "%" "^" "*" "+" "="]
   ["-" "/" ":" ";" "(" ")" "$" "&" "@" "\""]
   ["_" "\\" "|" "~" "<" ">" "€" "£" "¥" "•"]
   ["." "," "?" "!" "'"]])

(def mn-key-list-n
  [["1" "2" "3" "4" "5" "6" "7" "8" "9" "0"]
   ["[" "]" "{" "}" "#" "%" "^" "*" "+" "="]
   ["-" "/" ":" ";" "(" ")" "$" "&" "@" "\""]
   ["_" "\\" "|" "~" "<" ">" "€" "£" "¥" "•"]
   ["." "," "?" "!" "'"]])

(defn toolkit-row [alter alter-num opts]
  [keycommon/key-row
   [[keycommon/key-button {:flex 1.5} #(reset! state/alter-num (not @state/alter-num))
     [rn/text {} (if (true? alter-num) "ABC" "123")]]
    [keycommon/key-button {:flex 1.5} #(reset! state/alter (not @state/alter))
     [ui/ion-icons {:name "globe" :color "gray" :size 30}]]
    (if-not (true? alter)
      [keycommon/key-button {} #(bridge/editor-insert "᠂")
       [text/rotated-text {:font-family "MongolianBaiZheng" :font-size 18} 28 28 "᠂"]])
    [keycommon/key-button (if (false? alter) {:flex 3.5} {:flex 5.5}) #(bridge/editor-insert " ")
     [ui/ion-icons {:name "ios-scan" :color "gray" :size 30}]]
    (if-not (true? alter)
      [keycommon/key-button {} #(bridge/editor-insert "᠃")
       [text/rotated-text {:font-family "MongolianBaiZheng" :font-size 18} 28 28 "᠃"]])
    [keycommon/key-button {:flex 1.5} #(condp = (:type opts)
                                         "single-line"
                                         ((:on-press opts))

                                         (bridge/editor-insert "\n"))
     [ui/ion-icons {:name (condp = (:type opts)
                            "single-line" "ios-send"

                            "ios-return-down-back-sharp")
                    :color (condp = (:type opts)
                             "single-line" "#34d399"

                             "gray")
                    :size 30}]]]])

(defn mn-layout-a [s sn a an opts]
  [nbase/box  style/layout-box-style;{:style key-box-style}
   (for [k (take 2 mn-key-list)]
     ^{:key k}
     [keycommon/key-row
      (for [kk k]
        ^{:key kk}
        [keycommon/key-button {} #(candidates/candidates-query (:code kk))
         [text/rotated-text {:font-family "MongolianBaiZheng" :font-size 18} 28 28 (:label kk)]])])
   [keycommon/key-row
    [
     [keycommon/key-button {:flex 1.5} identity;#(dispatch [:keyboard-alter])
      [ui/ion-icons {:name "ios-arrow-up-circle-outline" :color "gray" :size 30}]]
     (for [kk (nth mn-key-list 2)]
       ^{:key kk}
       [keycommon/key-button {} #(candidates/candidates-query (:code kk))
        [text/rotated-text {:font-family "MongolianBaiZheng" :font-size 18} 28 28 (:label kk)]])
     [keycommon/key-button {:flex 1.5} #(candidates/candidates-delete)
      [ui/ion-icons {:name "backspace" :color "gray" :size 30}]]]]
   [toolkit-row a an opts]])

(defn mn-layout-n [s sn a an opts]
  [nbase/box style/layout-box-style
   [keycommon/key-row
    (for [kk (nth en-key-list-n (if (true? sn) 1 0))]
      ^{:key kk}
      [keycommon/key-char-button kk])]
   [keycommon/key-row
    (for [kk (nth en-key-list-n (if (true? sn) 3 2))]
      ^{:key kk}
      [keycommon/key-char-button kk])]
   [keycommon/key-row
    [
     [keycommon/key-button {:flex 1.65} #(reset! @state/shift-num (not @state/shift-num))
      [nbase/text {} (if (true? sn) "123" "#+=")]]
     (for [kk (nth en-key-list-n 4)]
       ^{:key kk}
       [keycommon/key-char-button (if (true? s) (str/upper-case kk) kk)])
     [keycommon/key-button {:flex 1.65} #(bridge/editor-delete)
      [ui/ion-icons {:name "backspace" :color "gray" :size 30}]]]]
   [toolkit-row a an opts]])


;; ----------------------------------------------------------------------
(defn en-layout-n [s sn a an opts]
  [nbase/box style/layout-box-style
   [keycommon/key-row
    (for [kk (nth en-key-list-n (if (true? sn) 1 0))]
      ^{:key kk}
      [keycommon/key-char-button kk])]
   [keycommon/key-row
    (for [kk (nth en-key-list-n (if (true? sn) 3 2))]
      ^{:key kk}
      [keycommon/key-char-button kk])]
   [keycommon/key-row
    [
     [keycommon/key-button {:flex 1.65} #(reset! state/shift-num (not @state/shift-num))
      [nbase/text {} (if (true? sn) "123" "#+=")]]
     (for [kk (nth en-key-list-n 4)]
       ^{:key kk}
       [keycommon/key-char-button (if (true? s) (str/upper-case kk) kk)])
     [keycommon/key-button {:flex 1.65} #(bridge/editor-delete)
      [ui/ion-icons {:name "backspace" :color "gray" :size 30}]]]]
   [toolkit-row a an opts]])

(defn en-layout-a [s sn a an opts]
  [nbase/box style/layout-box-style
   [keycommon/key-row
    (for [kk (nth en-key-list 0)]
      ^{:key kk}
      [keycommon/key-char-button (if (true? s) (str/upper-case kk) kk)])]
   ;
   ; [into]
   [nbase/flex {:flex 1 :w "100%"
                 :alignItems "center"
                 :justifyContent "center"}
    [nbase/flex { :flex-direction "row"
                  :alignItems "center"
                  :justifyContent "center"
                  :width "90%"}
     (for [kk (nth en-key-list 1)]
       ^{:key kk}
       [keycommon/key-char-button (if (true? s) (str/upper-case kk) kk)])]]
   [keycommon/key-row
    [[keycommon/key-button {:flex 1.65} #(reset! state/shift (not @state/shift))
      [ui/ion-icons {:name "ios-arrow-up-circle-outline" :color "gray" :size 30}]]
     (for [kk (nth en-key-list 2)]
       ^{:key kk}
       [keycommon/key-char-button (if (true? s) (str/upper-case kk) kk)])
     [keycommon/key-button {:flex 1.65} #(bridge/editor-delete)
      [ui/ion-icons {:name "backspace" :color "gray" :size 30}]]]]
   [toolkit-row a an opts]])

;; ----------------------------------------------------------------------
;; kt = keyboardType chat or other
(defn mn-layout [params opts]
  (fn []
    (let [{shift :shift
           shift-num :shift-num
           alter :alter
           alter-num :alter-num}
          params
          kt (:type params)]
      (if (true? @alter-num)
        [mn-layout-n @shift @shift-num @alter @alter-num opts]
        [mn-layout-a @shift @shift-num @alter @alter-num opts]))))

(defn en-layout [params opts]
  (fn []
    (let [{shift :shift
           shift-num :shift-num
           alter :alter
           alter-num :alter-num}
          params
          kt (:type params)]
      (if (true? @alter-num)
        [en-layout-n @shift @shift-num @alter @alter-num opts]
        [en-layout-a @shift @shift-num @alter @alter-num opts]))))
