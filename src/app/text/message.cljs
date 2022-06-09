(ns app.text.message
  (:require
   [cljs-bean.core :refer [bean ->clj ->js]]
   [applied-science.js-interop :as j]))


(def labels
  {:question
    {:title-placeholder "ᠠᠰᠠᠭᠤᠯᠲᠠ ᠪᠠᠨ ᠣᠷᠤᠭᠤᠯᠤᠭᠠᠳ ᠠᠰᠠᠭᠤᠯᠲᠠ ᠶᠢᠨ ᠲᠡᠮᠳᠡᠭ ᠶᠢᠡᠷ ᠲᠡᠭᠦᠰᠭᠡᠨ ᠡ"
     :content-placeholder "ᠠᠰᠠᠭᠤᠯᠲᠠ ᠶᠢᠨ ᠲᠠᠯ ᠠ ᠪᠡᠷ ᠨᠡᠮᠡᠯᠲᠡ ᠲᠠᠢᠯᠪᠦᠷᠢ ᠵᠢ ᠡᠨᠳᠡ ᠣᠷᠤᠭᠤᠯᠤᠨ ᠠ᠂ ᠲᠠ ᠬᠠᠷᠢᠭᠤᠯᠲᠠ ᠵᠢ ᠢᠯᠡᠭᠦᠦ ᠬᠤᠷᠳᠤᠨ ᠣᠯᠬᠤ ᠪᠣᠯᠤᠮᠵᠢᠲᠠᠢ(ᠰᠤᠩᠭᠤᠨ ᠲᠠᠭᠯᠠᠬᠤ)"
     :close-similar-titles "ᠠᠳᠠᠯᠢᠪᠲᠤᠷ ᠠᠰᠠᠭᠤᠯᠲᠠ ᠵᠢ ᠬᠠᠭᠠᠬᠤ"
     :vote "ᠵᠦᠪᠰᠢᠶᠡᠷᠡᠭᠰᠡᠨ"
     :all-answer-comments "《ᠪᠦᠬᠦ ᠰᠡᠳᠭᠡᠭᠳᠡᠯ ᠢ ᠦᠵᠡᠬᠦ》"}
   :search {:recent-search "ᠣᠷᠴᠢᠮ ᠤᠨ ᠬᠠᠢᠯᠲᠠ"
            :search-to-find "ᠬᠠᠯᠠᠮᠰᠢᠯ ᠬᠠᠢᠯᠲᠠ"}})
