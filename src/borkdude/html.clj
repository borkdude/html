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

(defn ->attrs
  ([m base-map]
   (let [m (merge base-map m)]
     (->attrs m)))
  ([m]
   (if (contains? m :&)
     `(->attrs ~(get m :&) ~(dissoc m :&))
     (str/join " "
               (map (fn [[k v]]
                      (str (name k)
                           "=" (cond (string? v) (pr-str v)
                                     (keyword? v) (pr-str (name v))
                                     (map? v) (pr-str (->css v))
                                     :else (str v))))
                    m)))))

(defn reader [form]
  (cond
    (and (vector? form)
         (keyword? (first form)))
    (let [[tag ?attrs & children] form
          tag (name tag)
          attrs? (map? ?attrs)
          children (if attrs? children (cons ?attrs children))
          attrs (if attrs?
                  (let [a (->attrs ?attrs)]
                    (if (string? a)
                      (str " " a)
                      a))
                  "")]
      `(str ~@(if (string? attrs)
                [(format "<%s%s>" tag attrs)]
                ["<" tag " " attrs  ">"])
            ~@(map #(list `html %) children)
            ~(format "</%s>" tag)))
    (string? form) (escape-html form)
    (number? form) form
    :else `(inspect ~form)))

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
  )
