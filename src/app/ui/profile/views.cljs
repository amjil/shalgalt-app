(ns app.ui.profile.views
  (:require
    [re-frame.core :as re-frame]
    [reagent.core :as reagent]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [steroid.rn.core :as rn]
    [app.ui.text :as text]
    [app.ui.nativebase :as nbase]
    [app.ui.editor :as editor]
    [app.ui.components :as ui]
    [app.ui.keyboard.index :as keyboard]
    [app.ui.keyboard.candidates :as candidates]
    [app.ui.keyboard.bridge :as bridge]
    ["react-native-vector-icons/Ionicons" :default Ionicons]))

(def profiles (reagent/atom [{:name "ᠨᠡᠷ᠎ᠡ" :value "ᠰᠡᠴᠡᠨᠪᠦᠬᠡ" :type "text"}
                             {:name "ᠬᠦᠢᠰᠦ" :value "ᠡᠷᠡᠭᠲᠡᠢ" :type "select" :title "gender"}
                             {:name "ᠲᠦᠷᠦᠭᠰᠡᠨ ᠡᠳᠦᠷ" :value "2020-01-01" :type "text"}
                             {:name "ᠢᠨᠠᠭ" :value "ᠤᠷᠤᠭᠯᠠᠪᠠ" :type "select" :title "marital"}
                             {:name "ᠳᠤᠷ᠎ᠠ ᠪᠠᠬ᠎ᠠ" :value "ᠬᠦᠯ ᠪᠦᠮᠪᠦᠭᠡ" :type "text"}
                             {:name "ᠨᠤᠲᠤᠭ" :value "ᠵᠠᠷᠤᠳ" :type "text"}
                             {:name "ᠠᠵᠢᠯ" :value "IT" :type "text"}
                             {:name "ᠲᠠᠨᠢᠯᠴᠠᠭᠤᠯᠭ᠎ᠠ" :value "" :type "text"}
                             {:name "ᠦᠪᠡᠷᠮᠢᠴᠡ ᠦᠬᠡ" :value "" :type "text"}]))

(def cursor (reagent/atom 0))

(def gender ["ᠡᠷᠡᠭᠲᠡᠢ" "ᠡᠮᠡᠭᠲᠡᠢ" "ᠨᠢᠭᠤᠴᠠ"])

(def marital ["ᠨᠢᠭᠤᠴᠠ" "ᠭᠠᠭᠴᠠ ᠪᠡᠶ᠎ᠡ" "ᠦᠶᠡᠷᠬᠡᠪᠡ" "ᠤᠷᠤᠭᠯᠠᠪᠠ"])

(def actionsheet-data (reagent/atom {}))

(def isopen (reagent/atom false))

(defn profile []
  [nbase/hstack {:bg "white" :h "100%"}
   [nbase/vstack {:h "100%"
                  :mx 4
                  :px 2
                  :borderLeftWidth "1"
                  :borderRightWidth "1"
                  :borderColor "gray.200"}
    [nbase/box {:mt 2 :p "6" :borderRadius "md" :bg "primary.200"}]
    [nbase/box {:mt 2
                :ml 1
                :justifyContent "center" :alignSelf "center" :alignItems "center"}
     [text/measured-text {:fontSize 18 :fontFamily "MongolianBaiZheng"} "ᠰᠡᠴᠡᠨᠪᠦᠬᠡ"]]
    [nbase/flex {:justifyContent "flex-end"
                 :flex 1}
     [nbase/box {:p "4" :bg "primary.200"
                 :mb 2}]
     [nbase/icon {:as Ionicons :name "chevron-down"
                  :justifyContent "center" :alignSelf "center"}]]]
   [nbase/actionsheet {:isOpen @isopen :onClose #(reset! isopen false) :safeArea true}
    [nbase/actionsheet-content {:maxH 300}
     [nbase/container {:flexDirection "row"}
      [nbase/box {:h "100%" :w 12 :py 4 :justifyContent "center"}
       [text/measured-text (merge {:fontSize 16 :color "#71717a"} text/base-prop) (:name @actionsheet-data)]]
      (for [x (:data @actionsheet-data)]
        ^{:key x}
        [nbase/actionsheet-item {:height 250 :w 12 :py 4
                                 :onPress (fn [e]
                                            (reset! isopen false)
                                            (swap! profiles assoc-in [@cursor :value] x))}
         [text/measured-text (merge {:fontSize 16 :color "#1f2937"} text/base-prop) x]])]]]
   [nbase/flat-list
    {:keyExtractor    (fn [_ index] (str "profile-item-" index))
     :data      @profiles
     :renderItem (fn [x]
                   (let [{:keys [item index separators]} (j/lookup x)]
                     (reagent/as-element
                       [nbase/pressable {:style {:height "100%"}; :width 28}}
                                         :borderLeftWidth "1"
                                         :borderColor "gray.200"
                                         :on-press (fn [e]
                                                     (reset! cursor index)
                                                     (if (= "text" (j/get item :type))
                                                       (re-frame/dispatch [:navigate-to :profile-edit])
                                                       (do
                                                        (reset! isopen true)
                                                        (cond
                                                          (= "gender" (j/get item :title))
                                                          (reset! actionsheet-data {:name (j/get item :name) :data gender})

                                                          (= "marital" (j/get item :title))
                                                          (reset! actionsheet-data {:name (j/get item :name) :data marital})))))}
                        [nbase/box {:h "20%"
                                    :mx 2
                                    :pl 2
                                    :pt 4}
                         [text/measured-text {:fontFamily "MongolianBaiZheng" :fontSize 18
                                              :color "#71717a"}
                           (j/get item :name)]]
                        [nbase/divider {:bg "gray.200" :thickness "1"
                                        :w "100%"}]
                        [nbase/box {:mx 2
                                    :pl 2
                                    :pt 4}
                         [text/measured-text {:fontFamily "MongolianBaiZheng" :fontSize 18
                                              :color "#71717a"}
                           (j/get item :value)]]])))

     :w "auto"
     :ml 2
     :px 2
     :horizontal true}]])

(defn edit-view []
  [ui/safe-area-consumer
   [nbase/flex {:flex 1
                :justifyContent "space-between"}
    [editor/editor-view
      {:type :text}
      ;content-fn
      (fn []
        (get-in @profiles [@cursor :value]))
      ;update-fn
      (fn [x]
        (swap! profiles assoc-in [@cursor :value] (get x :text)))]
    [candidates/views]
    [nbase/box {:height 220}
     [keyboard/keyboard]]]])

(def profile-edit
  {:name       :profile-edit
   :component  edit-view
   :options
   {:title ""
    :headerRight
    (fn [tag id classname]
      (reagent/as-element
        [nbase/icon-button {:variant "ghost" :colorScheme "indigo"
                            :icon (reagent/as-element [nbase/icon {:as Ionicons :name "ios-checkmark"}])
                            :on-press (fn [e] (js/console.log "on press icon button")
                                        (bridge/editor-content)
                                        (re-frame/dispatch [:navigate-back]))}]))}})
