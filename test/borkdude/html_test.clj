(ns borkdude.html-test
  (:require
   [borkdude.html :as sut]
   [clojure.test :as t]))

(t/deftest ok
  (t/are [expected actual]
      (= expected actual)

    "<div></div>"
    #html [:div]

    "<a href=\"#\">Hi</a>"
    #html [:a {:href "#"} "Hi"]

    ;; XHTML5?
    "<hr></hr>"
    #html [:hr]

    "<div><div>1</div><div>2</div></div>"
    #html [:div (for [i [1 2]]
                  #html [:div i])]

    #_#_
    "<div>&lt;script&gt;</div>"
    #html [:div "<script>"]))
