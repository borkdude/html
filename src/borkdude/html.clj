(ns borkdude.html
  (:require [clojure.string :as str]))

(defn inspect [x]
  (if (and (not (string? x))
           (sequential? x))
    (str/join "" x)
    x))

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

(defn html [form]
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
            ~@(map html children)
            ~(format "</%s>" tag)))

    (or (string? form)
        (number? form)) form
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
