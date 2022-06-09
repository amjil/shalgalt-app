(ns app.ui.text
  (:require
    [app.ui.nativebase :as nbase]
    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    ["react-native-measure-text-chars" :as rntext]
    ["native-base" :refer [useThemeProps useStyledSystemPropsResolver]]

    [steroid.rn.core :as rn]
    [steroid.rn.components.list :as rn-list]))

(def base-prop {:fontFamily "MongolianBaiZheng"})

(defn rotated-text [props width height t]
  (let [offset (- (/ height 2) (/ width 2))]
    [rn/text {:style (merge {:width height :height width
                             :transform [{:rotate "90deg"}
                                         {:translateX offset}
                                         {:translateY offset}]}
                            props)}
      t]))

(defn measured-text
  [props t]
  (let [props (if (and (nil? (:fontFamily props)) (nil? (:font-family props)))
                (merge props base-prop)
                props)
        info (rntext/measure (bean/->js (assoc props :text (if (empty? t) "A" t))))
        height (j/get info :width)
        width (j/get info :height)]
    (cond
      (nil? info)
      [rn/text "empty ...."]

      (= 1 (j/get info :lineCount))
      [rn/view {:style {:width width
                        :height height}}
       [rotated-text (dissoc props :width) width height (if (empty? t) "" t)]]

      :else
      (let [line-height (/ (j/get info :height) (j/get info :lineCount))]
        [rn/view {:style {:height height :width (* (inc line-height) (j/get info :lineCount))
                          :alignItems :center}}
        ; [rn/view {:style {:height height :width 300}}
         [rn-list/flat-list
          {:horizontal true
           :keyExtractor    (fn [_ index] (str "m-text-" index))
           ; :style {;:width (* (inc line-height) (j/get info :lineCount))
                   ; :flex 1
                   ; :width (* (j/get info :height) (j/get info :lineCount))
                   ; :height height}
           :contentContainerStyle {:flex 1
                                   :alignItems "center"}
           :style {:flexDirection "row"}
           :data (map (fn [x] (subs t (j/get x :start) (j/get x :end))) (j/get info :lineInfo))
           :scrollEnabled false
           :renderItem
           (fn [x]
             (let [{:keys [item index separators]} (j/lookup x)]
               (reagent/as-element
                 [rn/view {:style {:height height
                                   :width (inc line-height)}}
                  [rotated-text (dissoc props :width) (inc line-height) height item]])))}]]))))


    ; [rn/view {:style {:width width
    ;                   :height height}}
    ;  [rotated-text props width height (if (empty? t) "" t)]]))

(defn simple-text
  [props t]
  (let [props (if (and (nil? (:fontFamily props)) (nil? (:font-family props)))
                (merge props base-prop)
                props)
        info (rntext/measure (bean/->js (assoc props :text (if (empty? t) "A" t))))
        height (j/get info :width)
        width (+ 1 (j/get info :height))]
    (cond
      (nil? info)
      [rn/text "empty ...."]

      (= 1 (j/get info :lineCount))
      [rn/view {:style {:width width
                        :height height}}
       [rotated-text (dissoc props :width) width height (if (empty? t) "" t)]]

      :else
      (let [line-height (/ (j/get info :height) (j/get info :lineCount))
            data (as-> (map (fn [x] (subs t (j/get x :start) (j/get x :end))) (j/get info :lineInfo)) m
                   (take 2 m)
                   (concat m ["᠁ ᠁ ᠁   ᠁ ᠁ ᠁    ᠁ ᠁ ᠁"]))
            item-height (if (:width props) (:width props) height)]
        [rn/view {:style {:height item-height :width (* (inc line-height) 3)
                          :flexDirection "row"}}
         (for [x data]
           ^{:key x}
           [rn/view {:style {:height height
                             :width (inc line-height)}}
            [rotated-text (dissoc props :width) (inc line-height) item-height x]])]))))

(defn single-line-text
  [props t]
  (let [props (if (and (nil? (:fontFamily props)) (nil? (:font-family props)))
                (merge props base-prop)
                props)
        info (rntext/measure (bean/->js (assoc props :text (if (empty? t) "A" t))))
        height (j/get info :width)
        width (+ 1 (j/get info :height))]
    (cond
      (nil? info)
      [rn/text "empty ...."]

      (= 1 (j/get info :lineCount))
      [rn/view {:style {:width width
                        :height height}}
       [rotated-text (dissoc props :width) width height (if (empty? t) "" t)]]

      :else
      (let [line-height (/ (j/get info :height) (j/get info :lineCount))
            data (as-> (map (fn [x] (subs t (j/get x :start) (j/get x :end))) (j/get info :lineInfo)) m
                   (first m))]
        [rn/view {:style {:height height
                          :width (inc line-height)}}
         [rotated-text (dissoc props :width) (inc line-height) height data]]))))

(defn multi-line-text
  [props t]
  (let [props (if (and (nil? (:fontFamily props)) (nil? (:font-family props)))
                (merge props base-prop)
                props)
        info (rntext/measure (bean/->js (assoc props :text (if (empty? t) "A" t))))
        height (j/get info :width)
        width (+ 1 (j/get info :height))]
    (cond
      (nil? info)
      [rn/text "empty ...."]

      (= 1 (j/get info :lineCount))
      [rn/view {:style {:width width
                        :height height}}
       [rotated-text (dissoc props :width) width height (if (empty? t) "" t)]]

      :else
      (let [line-height (/ (j/get info :height) (j/get info :lineCount))
            data (as-> (map (fn [x] (subs t (j/get x :start) (j/get x :end))) (j/get info :lineInfo)) m)]
        (into
          [rn/view {:style {:height height :width (* (inc line-height) (j/get info :lineCount))
                            :flexDirection "row"}}]
          (for [x data]
            ^{:key x}
            [rn/view {:style {:height height
                              :width (inc line-height)}}
             [rotated-text (dissoc props :width) (inc line-height) height x]]))))))

(defn theme-text-props [name props]
  (let [theme-props (bean/->js (useThemeProps name (bean/->js props)))
        [text-props _] (useStyledSystemPropsResolver (bean/->js (j/get theme-props :_text)))]
    text-props))

(defn theme-props [name props]
  (let [theme-props (bean/->js (useThemeProps name (bean/->js props)))
        [text-props _] (useStyledSystemPropsResolver (bean/->js theme-props))]
    [(bean/->clj theme-props) (bean/->clj text-props)]))

(defn styled-text-view [props t]
  (let [[text-props _] (useStyledSystemPropsResolver (bean/->js props))]
    [measured-text (bean/->clj text-props) t]))
