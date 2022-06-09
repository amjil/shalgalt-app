(ns app.ui.keyboard.style)

(def key-style
  {
          :flex-direction "row"
          :flex 1
          :justifyContent "center"
          :alignItems "center"
          :backgroundColor "#FFF"
          :borderRightColor "#e8e8e8"
          :borderRightWidth 1
          :borderBottomColor "#e8e8e8"
          :borderBottomWidth 1})
          ; :height 38})

(def key-con-style
  {:backgroundColor "#FFF"
   :borderRightColor "#e8e8e8"
   :borderRightWidth 1
   :borderBottomColor "#e8e8e8"
   :borderBottomWidth 1
   :flex 1})

(def key-text-style
  {
    :fontWeight "400"
    :fontSize 25,
    :textAlign "center",
    :color "#222222"
    :width 42})

(def layout-box-style
  {:bg "blueGray.400"
   :w "100%"
   :h "100%"
   :borderTopWidth 1
   :borderColor "gray.500"
   :justify "center"
   :align "center"
   :flex 1})
