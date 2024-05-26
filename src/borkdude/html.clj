(ns borkdude.html
  (:require [clojure.string :as str]))

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

(defn inspect [x]
  (cond
    (string? x) (escape-html x)
    (sequential? x)
    (str/join "" x)
    :else x))

(defn- ->css [m]
  (str/join "\n"
            (map (fn [[k v]]
                   (str (name k) ": " (name v) ";"))
                 m)))

(defn- ->attrs [m]
  (str/join " "
            (map (fn [[k v]]
                   (str (name k)
                        "=" (cond (string? v) (pr-str v)
                                  (keyword? v) (pr-str (name v))
                                  (map? v) (pr-str (->css v))
                                  :else (str v))))
                 m)))

(defmacro html [form]
  (cond
    (and (vector? form)
         (keyword? (first form)))
    (let [[tag ?attrs & children] form
          tag (name tag)
          attrs? (map? ?attrs)
          children (if attrs? children (cons ?attrs children))
          attrs (if attrs?
                  (str " " (->attrs ?attrs))
                  "")]
      `(str ~(format "<%s%s>" tag attrs)
            ~@(map #(list `html %) children)
            ~(format "</%s>" tag)))
    (string? form) (escape-html form)
    (number? form) form
    :else `(inspect ~form)))

(comment
  (require '[clojure.walk :refer [macroexpand-all]])
  (def x
    (macroexpand-all
     '(let [name "Michiel"]
       (html [:div {:color :blue :style {:color :blue}}
              [:p "Hello there " name
               ;; "</a>" ;; TODO, this string should be escaped
               [:ul
                [:li 1]
                (map (fn [i]
                       (html [:li i]))
                     [2 3 4])]]])))))
