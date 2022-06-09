(ns app.ui.editor
  (:require
    [app.ui.nativebase :as nbase]
    [app.ui.components :as ui]
    [app.ui.html :as html]
    [app.ui.text :as text]
    [app.ui.basic.theme :as theme]
    [app.handler.gesture :as gesture]

    [applied-science.js-interop :as j]
    [cljs-bean.core :as bean]
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    ["react-native-webview" :refer [WebView]]
    ; ["@react-native-clipboard/clipboard" :default Clipboard]
    ["react-native" :as rn :refer [Dimensions Clipboard]]
    ["react-native-svg" :as svg]
    ["react-native-smooth-blink-view" :default blinkview])
  (:import
   [goog.async Debouncer]))

(defn simple-view [opts content-fn tap-fn]
  (let [webview-width (reagent/atom 2)
        webref (reagent/atom nil)
        init-selection-fn (Debouncer. (fn []
                                       (j/call @webref :postMessage
                                         (j/call js/JSON :stringify (clj->js {:type "initSelection" :message ""})))))
        on-message (fn [e]
                     (let [data (js->clj (j/call js/JSON :parse (j/get-in e [:nativeEvent :data]))
                                   :keywordize-keys true)]
                       (condp = (:type data)
                         "initHeight" (do
                                        (js/console.log (bean/->js data))
                                        ; (reset! webview-width (:message data)))
                                        (.fire init-selection-fn))
                         "onChange" (do
                                      (js/console.log (bean/->js data)))
                         "onContent" (do
                                       (js/console.log "editor on content >>>> " (clj->js data)))
                         "updateSelection" (do
                                             (js/console.log (j/get-in e [:nativeEvent :data]))
                                             ;; if there is has :offsetX change the view's width
                                             (let [webview-text-width (-> data :message :offsetX)]
                                               (when-not (nil? webview-text-width)
                                                 (js/console.log "webview text width = " webview-text-width)
                                                 (reset! webview-width (+ (get-in data [:message :width]) webview-text-width))))))))

        dv (reagent/atom (content-fn))]
    (fn []
      (when (and @webref (not= @dv (content-fn)))
        ; (js/console.log "update then content")
        (j/call @webref :postMessage
          (j/call js/JSON :stringify (clj->js {:type "setContent" :message (content-fn)})))
        (reset! dv (content-fn)))
      [gesture/gesture-root-view
       {:flex 1}
       [gesture/tap-gesture-handler
        {
          :onHandlerStateChange #(let [state (j/get-in % [:nativeEvent :state])]
                                   ; (js/console.log "tap gesture on scroll view" (j/get-in % [:nativeEvent :state]))
                                   (if (gesture/tap-state-end (j/get % :nativeEvent))
                                     (do
                                       (tap-fn))))}
                                       ; (js/console.log "tap gesture" (j/get % :nativeEvent)))))}
        [nbase/scroll-view {:flex 1 :_contentContainerStyle {:flexGrow 1 :width @webview-width}
                                                             ; :backgroundColor (theme/color "white" "#27272a")}
                            :horizontal true
                            :on-press (fn [e] (js/console.log "scroll-view on press"))
                            :scrollEventThrottle 16}
         [nbase/box {:style {:width @webview-width :height "100%"}
                     :pointerEvents "none"}
          [:> WebView (merge {:useWebKit true
                              :ref (fn [r] (reset! webref r))
                              :cacheEnabled false
                              :scrollEnabled false
                              :scrollEventThrottle 10
                              :hideKeyboardAccessoryView true
                              :keyboardDisplayRequiresUserAction false
                              :originWhitelist ["*"]
                              :startInLoadingState true
                              :bounces false
                              :javaScriptEnabled true
                              :source {:html html/quill-html
                                       :baseUrl ""}
                              :focusable false
                              :onMessage on-message
                              :injectedJavaScript
                              (str
                                ; " quill.root.innerHTML = \"" (content-fn) "\";"
                                (if (= (:type opts) :text)
                                  (let [content-value (j/call js/JSON :stringify (content-fn))]
                                    (str " quill.setText('" (subs content-value 1 (dec (count content-value))) "');"))
                                  (str " quill.root.innerHTML = \"" (content-fn) "\";"))
                                ; " _postMessage({type: 'initHeight', message: Math.max(document.body.offsetWidth, document.body.scrollWidth)});")
                                " _postMessage({type: 'initHeight', message: quill.root.ownerDocument.body.scrollWidth });")
                              :style {:height "100%"
                                      :width "100%"
                                      :backgroundColor "transparent"}
                              :pointerEvents "none"}
                          (if (theme/is-dark?)
                            {:forceDarkOn true}))]]]]])))

(def webref (reagent/atom nil))
(def weblen (reagent/atom 0))
(def cursor (reagent/atom nil))
(def is-caret (reagent/atom nil))

(defn editor-view [opts content-fn change-fn]
  (let [webview-width (reagent/atom 2)
        screen-width (.-width (.get Dimensions "window"))
        scroll-position (reagent/atom 0)
        scroll-ref (reagent/atom nil)
        cursor-dot (reagent/atom false)
        cursor-dot-fn (Debouncer. (fn [] (reset! cursor-dot false)) 2000)
        cursor-dot-delay (reagent/atom false)
        cursor-dot-delay-fn (Debouncer. (fn [] (reset! cursor-dot-delay false)) 500)
        init-selection-fn (Debouncer. (fn []
                                       (j/call @webref :postMessage
                                         (j/call js/JSON :stringify (clj->js {:type "initSelection" :message ""}))))
                              200)
        range (reagent/atom nil)
        is-menu (reagent/atom true)

        on-message (fn [e]
                     (let [data (js->clj (j/call js/JSON :parse (j/get-in e [:nativeEvent :data]))
                                   :keywordize-keys true)]
                       (condp = (:type data)
                         "initHeight" (do
                                        (js/console.log "initHeight .... " (bean/->js data))
                                        (reset! webview-width (:message data))
                                        (.fire init-selection-fn))
                         "onContent" (do
                                       (js/console.log "editor on content >>>> " (clj->js data))
                                       (reset! weblen (-> data :message :contentLength)))
                         "onChange" (do
                                      (change-fn (:message data)))
                                      ; (reset! content (:text (:message data))))
                                      ; (reset! webview-width (max (:width (:messge data)) screen-width)))
                         "updateSelection" (do
                                             (js/console.log (j/get-in e [:nativeEvent :data]))
                                             ;; if there is has :height change the view's width
                                             (let [webview-text-width (get data :height)]
                                               (when-not (nil? webview-text-width)
                                                 (reset! webview-width webview-text-width)))
                                             ; (let [webview-text-width (get-in data [:message :offsetX])]
                                             ;   (when-not (nil? webview-text-width)
                                             ;     (js/console.log "webview text width = " webview-text-width)
                                             ;     ; (reset! webview-width (:height data))
                                             ;     (reset! webview-width (+ (get-in data [:message :width]) webview-text-width))))
                                             (let [offset-x (- (-> data :message :offsetX) (-> data :message :width))]
                                               (when-not (nil? offset-x)
                                                 (js/console.log "webview offset-x = " offset-x)
                                                 (if @scroll-ref
                                                   (.scrollTo @scroll-ref (bean/->js {:x offset-x :animated true})))))
                                             (if (not= 0 (-> data :message :left))
                                               (reset! cursor (:message data)))
                                             (reset! is-caret true))
                         "copyText" (do (.setString Clipboard (:message data))
                                      (js/console.log (j/get-in e [:nativeEvent :data])))

                         "initRange" (do (js/console.log (j/get-in e [:nativeEvent :data])
                                           (reset! range (:message data)))
                                       (reset! is-caret false))
                         "updateRange" (do (js/console.log (j/get-in e [:nativeEvent :data]))
                                         (let [end-position (-> data :message :end)]
                                           (if (and (not= 0 (:left end-position)))
                                             (reset! range (:message data))))
                                         (reset! is-caret false)))))
        pan-start-location (reagent/atom nil)
        pan-translate (reagent/atom nil)
        options (.stringify js/JSON
                     (clj->js
                             {
                              :modules #js {:toolbar false}
                              :theme "snow"
                              :readOnly true}))]
                              ; :debug "info"
                              ; :placeholder "ᠠᠭᠤᠯᠭ᠎ᠠ ᠪᠠᠨ ᠨᠠᠢᠷᠠᠭᠤᠯᠤᠶ᠎ᠠ ..."}))]
    (fn []
      [gesture/gesture-root-view
       {:flex 1}
       [gesture/long-press-gesture-handler
        {:onHandlerStateChange (fn [e]
                                 (when (gesture/long-press-active (j/get e :nativeEvent))
                                   ; (js/console.log "long press >>>>>")
                                   ; (js/console.log (j/get e :nativeEvent))
                                   (j/call @webref :postMessage
                                     (j/call js/JSON :stringify (clj->js {:type "initRange" :message {:x (j/get-in e [:nativeEvent :x]), :y (j/get-in e [:nativeEvent :y])}})))))}


        [gesture/tap-gesture-handler
         {
           :onHandlerStateChange #(let [state (j/get-in % [:nativeEvent :state])]
                                    (cond
                                      (and (= 4 state) (true? @cursor-dot-delay))
                                      (do
                                        ; (js/console.log "cursor-dot delay run")
                                        (reset! cursor-dot true)
                                        (.fire cursor-dot-fn))

                                      (and (= 4 state) (false? @cursor-dot-delay))
                                      (do
                                        ; (js/console.log "cursor-dot delay prepare")
                                        (reset! cursor-dot-delay true)
                                        (.fire cursor-dot-delay-fn)))

                                    ; (js/console.log "tap gesture on scroll view" (j/get-in % [:nativeEvent :state]))
                                    (if (gesture/tap-state-end (j/get % :nativeEvent))
                                      (do
                                        ; (js/console.log "tap gesture" (j/get % :nativeEvent))
                                        (j/call @webref :postMessage
                                          (j/call js/JSON :stringify #js {:type "setSelection" :message #js {:x (+ (j/get-in % [:nativeEvent :x]) @scroll-position) :y (j/get-in % [:nativeEvent :y])}})))))}
         [nbase/scroll-view {:flex 1 :_contentContainerStyle {:flexGrow 1 :width @webview-width}
                             :horizontal true
                             :on-press (fn [e] (js/console.log "scroll-view on press"))
                             :scrollEventThrottle 16
                             :ref (fn [r]
                                    (reset! scroll-ref r))
                             :on-scroll (fn [e]
                                          ; (js/console.log "scroll-view-on-scroll")
                                          ; (js/console.log "on scroll >>>" (j/get-in e [:nativeEvent :contentOffset :x]))
                                          (reset! scroll-position (j/get-in e [:nativeEvent :contentOffset :x])))}
          ; [nbase/box {:style {:width @webview-width :height "100%"}}]
          [nbase/box {:style {:width @webview-width :height "100%"}
                      :pointerEvents "none"}
           [:> WebView (merge {:useWebKit true
                               :ref (fn [r] (reset! webref r))
                               :cacheEnabled false
                               :scrollEnabled false
                               :scrollEventThrottle 10
                               :hideKeyboardAccessoryView true
                               :keyboardDisplayRequiresUserAction false
                               :originWhitelist ["*"]
                               :startInLoadingState true
                               :bounces false
                               :javaScriptEnabled true
                               :source {:html html/quill-html
                                        :baseUrl ""}
                               :focusable false
                               :onMessage on-message
                               :injectedJavaScriptBeforeContentLoaded (str "window.options=" (j/call js/JSON :stringify (bean/->js opts)))
                               :injectedJavaScript
                               (str
                                 ; " document.querySelector('#editor').innerHTML=\"" content "\";"
                                 (if (= (:type opts) :text)
                                   (let [content-value (j/call js/JSON :stringify (content-fn))]
                                     (str " quill.setText('" (subs content-value 1 (dec (count content-value))) "');"))
                                   (str " quill.root.innerHTML = \"" (content-fn) "\";"))
                                 "_postMessage({type: 'initHeight', message: Math.max(document.body.offsetWidth, document.body.scrollWidth)});
                                   //var length = quill.getLength();
                                   //var range = pointFromSelection(length - 1);
                                   //_postMessage({type: 'updateSelection', message: range});
                                  ")
                               :style {:height "100%"
                                       ; :width "100%"
                                       :backgroundColor "transparent"
                                       :margin-bottom 10}
                               :pointerEvents "none"}
                         (if (theme/is-dark?)
                           {:forceDarkOn true}))]]
          (if (true? @is-caret)
            [nbase/box {:style {:top (:top @cursor) :left (:left @cursor)
                                :zIndex 30001
                                :position "absolute"}
                        :elevation 3001
                        :flex-direction "row"}
             [:> blinkview {"useNativeDriver" false}
              [:> svg/Svg {:width 18 :height 2}
               [:> svg/Rect {:x "0" :y "0" :width 18 :height 2 :fill "blue"}]]]
             (if (true? @cursor-dot)
               [gesture/tap-gesture-handler
                {:onHandlerStateChange #(do
                                          (if (gesture/tap-state-end (j/get % :nativeEvent))
                                            (do
                                              ; (js/console.log "caret dot tap gesture" (j/get % :nativeEvent))
                                              nil)))}
                [gesture/pan-gesture-handler
                 {:onGestureEvent
                  (fn [e] (let [x (+ (:left @pan-start-location) (j/get-in e [:nativeEvent :translationX]))
                                ; y (j/get-in e [:nativeEvent :translationY])
                                y (+ (:top @pan-start-location) (j/get-in e [:nativeEvent :translationY]))]
                            ; (js/console.log (j/get e :nativeEvent))
                            ; (js/console.log "x11 = " x " y = " y)
                            (.fire cursor-dot-fn)
                            (j/call @webref :postMessage
                              (j/call js/JSON :stringify #js {:type "setSelection" :message #js {:x x :y y}}))))
                  :onHandlerStateChange
                  (fn [e] (when (= 2 (j/get-in e [:nativeEvent :state]))
                            ; (js/console.log "caret dot set pan start location!!!!" (bean/->js @cursor))
                            (reset! pan-start-location @cursor)))}

                 [nbase/box {:style {:margin-top -9
                                     :margin-left 5}}
                  [nbase/box {
                              :w 5
                              :h 5
                              :border-top-radius "full"
                              :border-bottom-right-radius "full"
                              :bg "blue.600"
                              :style
                              {
                               :transform [{:rotate "45deg"}]}}]]]])])

          (if (false? @is-caret)
             [nbase/box {:style {:top (- (:top (:start @range)) 20) :left (+ (:left (:start @range)) 20)}
                         :position "absolute"}
              [gesture/pan-gesture-handler
               {:onGestureEvent
                (fn [e] (let [x (+ (:left @pan-start-location) (j/get-in e [:nativeEvent :translationX]))
                              y (+ (:top @pan-start-location) (j/get-in e [:nativeEvent :translationY]))]
                          ; (js/console.log "x = " x " y = " y)
                          (j/call @webref :postMessage
                            (j/call js/JSON :stringify (clj->js {:type "updateRange"
                                                                 :message
                                                                 {:start {:x x :y y}
                                                                  :end {:x (:left (:end @range))
                                                                        :y (:top (:end @range))}}})))))
                :onHandlerStateChange
                (fn [e]
                  (condp = (j/get-in e [:nativeEvent :state])
                    2 (do
                        (reset! is-menu false)
                        (reset! pan-start-location (:start @range)))
                    5 (do (reset! is-menu true))
                    (js/console.log "xxx " (j/get-in e [:nativeEvent :state]))))}
               [nbase/box {
                           :w 5
                           :h 5
                           :border-top-radius "full"
                           :border-bottom-right-radius "full"
                           :bg "blue.600"}]]])
          (if (false? @is-caret)
             [nbase/box {:style {:top (:top (:end @range))
                                 :left (+ (:left (:end @range)) 20)}
                         :position "absolute"}
              [gesture/tap-gesture-handler
               {:onHandlerStateChange #(do
                                         (if (gesture/tap-state-end (j/get % :nativeEvent))
                                           (do
                                             ; (js/console.log "range end tap gesture" (j/get % :nativeEvent))
                                             nil)))}
               [gesture/pan-gesture-handler
                {:onGestureEvent
                 (fn [e] (let [x (+ (:left @pan-start-location) (j/get-in e [:nativeEvent :translationX]))
                               y (+ (:top @pan-start-location) (j/get-in e [:nativeEvent :translationY]))]
                           ; (js/console.log "range end x = " x " y = " y)
                           (j/call @webref :postMessage
                             (j/call js/JSON :stringify (clj->js {:type "updateRange"
                                                                  :message
                                                                  {:start {:x (:left (:start @range))
                                                                           :y (:top (:start @range))}
                                                                   :end {:x x :y y}}})))))
                 :onHandlerStateChange
                 (fn [e]
                   (condp = (j/get-in e [:nativeEvent :state])
                     2 (do
                         (reset! is-menu false)
                         (reset! pan-start-location (:end @range)))
                     5 (do (reset! is-menu true))
                     (js/console.log "xxx " (j/get-in e [:nativeEvent :state]))))}
                [nbase/box {:w 5
                            :h 5
                            :border-bottom-radius "full"
                            :border-top-right-radius "full"
                            :bg "blue.600"}]]]])
          (if (false? @is-caret)
            (for [x (:ranges @range)]
              ^{:key (str "range-area-" (:top x) "-" (:left x))}
              [nbase/box {:style {:width (:width x)
                                  :height (:height x)
                                  :left (:left x)
                                  :top (:top x)}
                          :position "absolute"
                          :bg "blue.600:alpha.30"}]))
          (if (and (false? @is-caret) (true? @is-menu))
            (let [screen-width (.-width (.get Dimensions "window"))
                  end-left (- (:left (:end @range)) @scroll-position)
                  start-left (- (:left (:start @range)) @scroll-position)
                  left (cond
                         (and (neg? end-left) (neg? start-left))
                         @scroll-position

                         (and (> end-left screen-width) (> start-left screen-width))
                         @scroll-position

                         :else
                         (+ @scroll-position end-left 60))]

             [nbase/box {:style {:left left
                                 :top 20
                                 :padding 12}
                         :position "absolute"
                         :flex-grow 1
                         :flex-shrink 1
                         :flex-direction "column"
                         :shadow "9"
                         :bg "lightText"
                         :border-radius "md"
                         :justifyContent "flex-end"
                         :elevation 3001}
                         ; :w 10
              [gesture/tap-gesture-handler
               {:onHandlerStateChange
                (fn [e]
                  (when (gesture/tap-state-end (j/get e :nativeEvent))
                    ; (js/console.log "paste")
                    (.then (.getString Clipboard)
                      (fn [x]
                        (j/call @webref :postMessage
                          (j/call js/JSON :stringify
                            (bean/->js {:type "insertText" :message {:index (:index (:start @range))
                                                                     :text x}})))))))}
               [nbase/pressable
                [text/measured-text {:fontSize 18 :fontFamily "MongolianBaiZheng"} "ᠨᠠᠭᠠᠬᠤ"]]]
              [nbase/divider {:my 2}]
              [gesture/tap-gesture-handler
               {:onHandlerStateChange
                (fn [e]
                  (when (gesture/tap-state-end (j/get e :nativeEvent))
                    ; (js/console.log "copy")
                    (j/call @webref :postMessage
                      (j/call js/JSON :stringify
                        (bean/->js {:type "copyText"
                                    :message {:start (:index (:start @range))
                                              :end (:index (:end @range))}})))))}
               [nbase/pressable
                [text/measured-text {:fontSize 18 :fontFamily "MongolianBaiZheng"} "ᠬᠠᠭᠤᠯᠬᠤ"]]]
              [nbase/divider {:my 2}]
              [gesture/tap-gesture-handler
               {:onHandlerStateChange
                (fn [e]
                  (when (gesture/tap-state-end (j/get e :nativeEvent))
                    ; (js/console.log "delete")
                    (j/call @webref :postMessage
                      (j/call js/JSON :stringify
                        (bean/->js {:type "deleteText"
                                    :message {:start (:index (:start @range))
                                              :end (:index (:end @range))}})))))}
               [nbase/pressable
                [text/measured-text {:fontSize 18 :fontFamily "MongolianBaiZheng"} "ᠬᠠᠰᠤᠬᠤ"]]]
              [nbase/divider {:my 2}]
              [gesture/tap-gesture-handler
               {:onHandlerStateChange
                (fn [e]
                  (when (gesture/tap-state-end (j/get e :nativeEvent))
                    (j/call @webref :postMessage
                      (j/call js/JSON :stringify
                        (bean/->js {:type "selectAll"
                                    :message ""})))))}
               [nbase/pressable
                [text/measured-text {:fontSize 18 :fontFamily "MongolianBaiZheng"} "ᠪᠦᠭᠦᠳᠡ"]]]]))]]]])))
