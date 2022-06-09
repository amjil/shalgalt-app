(ns app.ui.keyboard.state
  (:require
    [reagent.core :as reagent]))


(def alter (reagent/atom nil))
(def shift (reagent/atom nil))

(def alter-num (reagent/atom nil))
(def shift-num (reagent/atom nil))
