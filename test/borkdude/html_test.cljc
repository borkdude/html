(ns borkdude.html-test
  (:require
   [borkdude.html :refer [html]]
   [clojure.test :as t]))

(defn child-component [{:keys [name]}]
  (html [:<> "Hello " name]))

(defn App []
  (html
   [:div
    [:div {:color :blue}]
    [:$ (child-component {:name "Michiel"})]]))

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

    "<div class=\"bar\" style=\"color: blue;\"></div>"
    (let [m {:style {:color :blue}
             :class "bar"}]
      (html [:div {:class "foo"
                   :& m}]))

    "<div class=\"&lt;script&gt;\"></div>"
    (html [:div {:class "<script>"}])

    "<script></script>"
    (html [:$ (str "<script>" "</script>")])

    "<div>hello there</div>"
    (html [:div [:<> "hello " "there"]])

    "<div><div color=\"blue\"></div>Hello Michiel</div>"
    (App)

    "<div>:&lt;script&gt;</div>"
    (html [:div [[:<script>]]])
    )

  )
