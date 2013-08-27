(ns lucuma.custom-elements
  (:require [lucuma.shadow-dom :as sd]
            [lucuma.util :refer [set-if-not-nil!]])
  (:refer-clojure :exclude [name]))

;;https://dvcs.w3.org/hg/webcomponents/raw-file/tip/spec/custom/index.html#concepts
(def ^:private forbidden-names #{"annotation-xml" "color-profile" "font-face" "font-face-src" "font-face-uri" "font-face-format" "font-face-name" "missing-glyph"})

(defn valid-name?
  [name]
  (and (not= -1 (.indexOf name "-"))
       (not (contains? forbidden-names name))))

(defmulti render-content (fn [c] (type c)))

(defmethod render-content js/String [s] s)

(defmethod render-content js/HTMLTemplateElement [t] (.cloneNode (aget t "content") true))

(defmethod render-content :default [c] (throw (str "No render-content implementation for " c) (ex-info {:type (type c)})))

(defmulti set-content! (fn [_ c] (type c)))

(defmethod set-content! js/String [sr s] (aset sr "innerHTML" s))

(defmethod set-content! js/HTMLElement [sr e] (.appendChild sr e))

(defmethod set-content! :default [sr c] (throw (str "No set-content! implementation for " c) (ex-info {:type (type c)})))

(defmulti set-style! (fn [_ c] (type c)))

(defmethod set-style! js/String [sr s] (let [style (.createElement js/document "style")]
                                         (aset style "innerHTML" s)
                                         (.appendChild sr style)))

(defn- init-content!
  [sr c]
  (if-let [rc (render-content c)]
    (set-content! sr rc)))

(defn- initialize
  [e content style reset-style-inheritance apply-author-styles]
  (when (or content style)
    (let [sr (sd/create e reset-style-inheritance apply-author-styles)]
      (when content (init-content! sr content))
      (when style (set-style! sr style)))))

(defn- find-prototype
  [t]
  (if t
    (if (instance? js/HTMLElement t)
      (.-prototype t)
      (.getPrototypeOf js/Object (.createElement js/document t)))
    (.-prototype js/HTMLElement)))

(defn- wrap-with-this-argument
  [f]
  (when f (fn [& args] (this-as e (apply f (conj args e))))))

(defn- set-callback!
  [proto name callback]
  (set-if-not-nil! proto name (wrap-with-this-argument callback)))

(defn- initialize-and-set-callback!
  [f m]
  (fn []
    (do
      (let [{:keys [content style reset-style-inheritance apply-author-styles]} m]
        (this-as e (initialize e content style reset-style-inheritance apply-author-styles)))
      (when-let [f (wrap-with-this-argument f)]
        (f)))))

(defn create-prototype
  [m]
  (let [{:keys [base-type created-fn entered-document-fn left-document-fn attribute-changed-fn]} m
        proto (.create js/Object (find-prototype base-type))]
    (aset proto "createdCallback" (initialize-and-set-callback! created-fn m))
    (set-callback! proto "enteredDocumentCallback" entered-document-fn)
    (set-callback! proto "leftDocumentCallback" left-document-fn)
    (set-callback! proto "attributeChangedCallback" attribute-changed-fn)
    proto))

(defn register
  ([m] (register (:name m) (create-prototype m) (:extends m)))
  ([name proto] (register name proto nil))
  ([name proto extends] {:pre [(valid-name? name)]} (.register js/document name (clj->js (merge {:prototype proto} (when extends {:extends extends}))))))

(defn create
  ([name] (create name nil))
  ([name is] (.createElement js/document name is)))