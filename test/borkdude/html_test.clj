(ns borkdude.html-test
  (:require
   [borkdude.html :refer [html]]
   [clojure.test :as t]))

(t/deftest ok
  (t/are [expected form]
      (= expected form)

    "<div></div>"
    (html [:div])

    "<div></div>"
    #html [:div]

    "<a href=\"#\">Hi</a>"
    (html [:a {:href "#"} "Hi"])

    ;; XHTML5?
    "<hr></hr>"
    (html [:hr])

    "<div>&lt;script&gt;</div>"
    (html [:div "<script>"])

    "<div>&lt;script&gt;</div>"
    (let [x "<script>"]
      (html [:div x]))

    ))
