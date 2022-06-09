(ns app.ui.nativebase
  (:require
   [applied-science.js-interop :as j]
   [cljs-bean.core :as bean]
   [reagent.core :as reagent]
   [steroid.rn.core :as rn]
   [steroid.rn.navigation.core :as rnn]
   [promesa.core :as p]
   [promesa.exec :as exec]
   [app.handler.gesture :as gesture]
   ["react-native-measure-text-chars" :as rntext]
   ["native-base" :refer [NativeBaseProvider
                          Center
                          Container
                          Box

                          PresenceTransition


                          Menu Menu.Item Menu.Group Menu.OptionGroup Menu.ItemOption
                          Popover Popover.Content Popover.Arrow Popover.CloseButton Popover.Header Popover.Body Popover.Footer
                          Modal Modal.Content Modal.CloseButton Modal.Header Modal.Body Modal.Footer
                          Heading

                          Alert Alert.Icon
                          Skeleton Skeleton.Text

                          Text
                          Button Button.Group
                          Input
                          Link
                          CloseIcon
                          IconButton
                          Icon

                          Badge
                          Pressable
                          CheckIcon
                          Select Select.Item
                          Actionsheet Actionsheet.Content Actionsheet.Item

                          Spacer
                          Divider
                          HStack
                          VStack
                          ZStack
                          Flex

                          FlatList
                          SectionList
                          ScrollView

                          Collapse
                          Spinner

                          ;;
                          HamburgerIcon

                          useStyledSystemPropsResolver
                          usePropsResolution
                          useThemeProps
                          useToast
                          useDisclose]]
   ["react" :as react]))


(def nativebase-provider (reagent/adapt-react-class NativeBaseProvider))

(def box (reagent/adapt-react-class Box))
(def badge (reagent/adapt-react-class Badge))

(def container (reagent/adapt-react-class Container))

(def heading (reagent/adapt-react-class Heading))

(def presence-transition (reagent/adapt-react-class PresenceTransition))

(def text (reagent/adapt-react-class Text))
(def input (reagent/adapt-react-class Input))

(def button (reagent/adapt-react-class Button))
(def button-group (reagent/adapt-react-class Button.Group))

(def link (reagent/adapt-react-class Link))
(def icon (reagent/adapt-react-class Icon))

(def icon-button (reagent/adapt-react-class IconButton))
(def close-icon (reagent/adapt-react-class CloseIcon))

(def select (reagent/adapt-react-class Select))
(def select-item (reagent/adapt-react-class Select.Item))
(def alert (reagent/adapt-react-class Alert))
(def alert-icon (reagent/adapt-react-class Alert.Icon))
(def collapse (reagent/adapt-react-class Collapse))
(def spinner (reagent/adapt-react-class Spinner))

(def skeleton (reagent/adapt-react-class Skeleton))
(def skeleton-text (reagent/adapt-react-class Skeleton.Text))
; Modal Modal.Content Modal.CloseButton Modal.Header Modal.Body Modal.Footer))
(def modal (reagent/adapt-react-class Modal))
(def modal-content (reagent/adapt-react-class Modal.Content))
(def modal-close-button (reagent/adapt-react-class Modal.CloseButton))
(def modal-header (reagent/adapt-react-class Modal.Header))
(def modal-body (reagent/adapt-react-class Modal.Body))
(def modal-footer (reagent/adapt-react-class Modal.Footer))

;Menu
; Menu Menu.Item Menu.Group Menu.OptionGroup Menu.ItemOption))
(def menu (reagent/adapt-react-class Menu))
(def menu-item (reagent/adapt-react-class Menu.Item))
(def menu-group (reagent/adapt-react-class Menu.Group))
(def menu-option-group (reagent/adapt-react-class Menu.OptionGroup))
(def menu-item-option (reagent/adapt-react-class Menu.ItemOption))

; Actionsheet Actionsheet.Content Actionsheet.Item
(def actionsheet (reagent/adapt-react-class Actionsheet))
(def actionsheet-content (reagent/adapt-react-class Actionsheet.Content))
(def actionsheet-item (reagent/adapt-react-class Actionsheet.Item))
(def checkicon (reagent/adapt-react-class CheckIcon))

(def spacer (reagent/adapt-react-class Spacer))

(def pressable (reagent/adapt-react-class Pressable))

(def flex (reagent/adapt-react-class Flex))

(def hstack (reagent/adapt-react-class HStack))
(def vstack (reagent/adapt-react-class VStack))
(def zstack (reagent/adapt-react-class ZStack))
(def divider (reagent/adapt-react-class Divider))
(def scroll-view (reagent/adapt-react-class ScrollView))
(def flat-list (reagent/adapt-react-class FlatList))
(def section-list (reagent/adapt-react-class SectionList))

(def center (reagent/adapt-react-class Center))
