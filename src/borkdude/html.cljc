(ns borkdude.html
  (:require [clojure.string :as str])
  #?(:cljs (:require-macros [borkdude.html :refer [html #_str*]])))

(deftype Html [s]
  Object
  (toString [_] s))

(defn- escape-html
  "From hiccup"
  [text]
  (->
   (.. ^String (str text)
       (replace "&"  "&amp;")
       (replace "<"  "&lt;")
       (replace ">"  "&gt;")
       (replace "\"" "&quot;")
       (replace "'" "&apos;" #_(if (= *html-mode* :sgml) "&#39;" "&apos;")))))

(defn ->safe [x]
  (cond
    (instance? Html x) (str x)
    (string? x) (escape-html x)
    (sequential? x) (str/join "" (map ->safe x))
    :else (escape-html x)))

(defn- ->css [m]
  (str/join "\n"
            (map (fn [[k v]]
                   (str (name k) ": " (name v) ";"))
                 m)))

(defn ->attrs
  ([m]
   (str/join " "
             (map (fn [[k v]]
                    (str (name k)
                         "=" (cond (string? v) (pr-str (escape-html v))
                                   (keyword? v) (pr-str (name v))
                                   (map? v) (pr-str (->css v))
                                   :else (str v))))
                  m)))
  ([m base-map]
   (let [m (merge base-map m)]
     (->attrs m))))

(defn- compile-attrs [m]
  (if (contains? m :&)
    `(->attrs ~(get m :&) ~(dissoc m :&))
    (->attrs m)))

#_(defmacro str* [& xs]
  (loop [acc ""
         xs (seq xs)]
    (if xs
      (let [x (first xs)
            xs (next xs)]
        (if (string? x)
          (recur (str acc x) xs)
          `(str ~acc ~x (str* ~@xs))))
      acc)))

(defn reader [form]
  (cond
    (nil? form) nil
    (and (vector? form)
         (keyword? (first form)))
    (let [[tag ?attrs & children] form
          tag (name tag)
          omit-tag? (= "<>" tag)
          attrs? (map? ?attrs)
          children (if attrs? children (cons ?attrs children))
          unsafe? (= "$" tag)
          attrs (if attrs?
                  (let [a (compile-attrs ?attrs)]
                    (if (string? a)
                      (str " " a)
                      a))
                  "")]
      (if unsafe?
        `(->Html (str ~(first children)))
        `(->Html (str ~@(if omit-tag?
                         nil
                         (if (string? attrs)
                           [(str "<" tag attrs ">")]
                           ["<" tag " " attrs  ">"]))
                       ~@(map #(if (string? %)
                                 %
                                 (list `html %)) children)
                     ~(if omit-tag?
                        nil
                        (str "</" tag ">"))))))
    (string? form) `(->Html ~(escape-html form))
    (number? form) form
    :else `(->Html (->safe ~form))))

(defmacro html [form]
  (reader form))

#_{:clj-kondo/ignore [:unused-referred-var
                      :unused-namespace]}
(comment
  (require '[clojure.walk :refer [macroexpand-all]])
  (let [m {:style {:color :blue}
           :class "bar"}]
    (html [:div {:class "foo"
                 :& m}]))
  (html [:div {:unsafeInnerHTML "<script>"}])
  (let [x "<script>"] (html [:div {:__unsafeInnerHTML x}]))
  (html [:$ (str "<script>" "</script>")])
  (html [:div [:<> "hello " "there"]])
  (html [:div [[:<>script]]])
  (html [:div [:$ [:<>script]]])
  (macroexpand-all '(html [:div ]))
  (macroexpand-all '(html [:div "Hello"]))
  )
