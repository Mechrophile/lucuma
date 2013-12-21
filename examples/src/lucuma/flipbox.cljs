(ns lucuma.flipbox
  (:require-macros [lucuma :refer [defwebcomponent]]))

(def style
  ":host{-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;display:block;position:relative;height:100%;width:100%;-webkit-transform-style:preserve-3d;-moz-transform-style:preserve-3d;-ms-transform-style:preserve-3d;-o-transform-style:preserve-3d;transform-style:preserve-3d}
   :host > *{position:absolute;top:0;left:0;width:100%;height:100%;-webkit-backface-visibility:hidden;-moz-backface-visibility:hidden;-ms-backface-visibility:hidden;-o-backface-visibility:hidden;backface-visibility:hidden;-webkit-transition-property:-webkit-transform;-moz-transition-property:-moz-transform;-ms-transition-property:-ms-transform;-o-transition-property:-o-transform;transition-property:transform;-webkit-transition-duration:.25s;-moz-transition-duration:.25s;-ms-transition-duration:.25s;-o-transition-duration:.25s;transition-duration:.25s;-webkit-transition-timing-function:linear;-moz-transition-timing-function:linear;-ms-transition-timing-function:linear;-o-transition-timing-function:linear;transition-timing-function:linear;-webkit-transition-delay:0;-moz-transition-delay:0;-ms-transition-delay:0;-o-transition-delay:0;transition-delay:0;-webkit-transform-style:preserve-3d;-moz-transform-style:preserve-3d;-ms-transform-style:preserve-3d;-o-transform-style:preserve-3d;transform-style:preserve-3d}
   :host > *:first-child{-webkit-transform:perspective(800px) rotateY(0) translate3d(0,0,2px);-moz-transform:perspective(800px) rotateY(0) translate3d(0,0,2px);-ms-transform:perspective(800px) rotateY(0) translate3d(0,0,2px);-o-transform:perspective(800px) rotateY(0) translate3d(0,0,2px);transform:perspective(800px) rotateY(0) translate3d(0,0,2px);z-index:2}
   :host > *:last-child{-webkit-transform:perspective(800px) rotateY(180deg) translate3d(0,0,1px);-moz-transform:perspective(800px) rotateY(180deg) translate3d(0,0,1px);-ms-transform:perspective(800px) rotateY(180deg) translate3d(0,0,1px);-o-transform:perspective(800px) rotateY(180deg) translate3d(0,0,1px);transform:perspective(800px) rotateY(180deg) translate3d(0,0,1px);z-index:1}
   :host[flipped] > *:first-child{-webkit-transform:perspective(800px) rotateY(180deg) translate3d(0,0,2px);-moz-transform:perspective(800px) rotateY(180deg) translate3d(0,0,2px);-ms-transform:perspective(800px) rotateY(180deg) translate3d(0,0,2px);-o-transform:perspective(800px) rotateY(180deg) translate3d(0,0,2px);transform:perspective(800px) rotateY(180deg) translate3d(0,0,2px);z-index:1}
   :host[flipped] > *:last-child{-webkit-transform:perspective(800px) rotateY(360deg) translate3d(0,0,1px);-moz-transform:perspective(800px) rotateY(360deg) translate3d(0,0,1px);-ms-transform:perspective(800px) rotateY(360deg) translate3d(0,0,1px);-o-transform:perspective(800px) rotateY(360deg) translate3d(0,0,1px);transform:perspective(800px) rotateY(360deg) translate3d(0,0,1px);z-index:2}")

(defn- set-flipped! [el v] (set! (.-flipped el) v))
(defn toggle [el] (set-flipped! el (not (.-flipped el))))
(defn show-front [el] (set-flipped! el false))
(defn show-back [el] (set-flipped! el true))

(defwebcomponent lucu-flipbox
  :content [:content]
  :style style
  :attributes #{:flipped}
  :methods {:showFront show-front :showBack show-back :toggle toggle}
  :apply-author-styles true)
