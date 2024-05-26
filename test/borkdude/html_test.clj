(ns borkdude.html-test
  (:require
   [borkdude.html :as sut]
   [clojure.test :as t]))

(t/deftest ok
  (t/are [expected form]
      (= expected (sut/html form))

    "<div></div>"
    [:div]

    "<a href=\"#\">Hi</a>"
    [:a {:href "#"} "Hi"]

    ;; XHTML5?
    "<hr></hr>"
    [:hr]

    #_#_
    "<div>&lt;script&gt;</div>"
    [:div "<script>"]))
