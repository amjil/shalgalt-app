(ns app.ui.keyboard.section
  (:require
   [app.ui.components :as ui]
   [app.ui.keyboard.candidates :as candidates]
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [clojure.string :as str]
   [app.ui.nativebase :as nbase]))

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
