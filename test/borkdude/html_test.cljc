(ns borkdude.html-test
  (:require
   [borkdude.html :refer [html xml]]
   [clojure.test :as t]))

(defn child-component [{:keys [name]}]
  (html [:<> "Hello " name]))

(defn App []
  (html
   [:div
    [:div {:color :blue}]
    (child-component {:name "Michiel"})]))

(t/deftest ok
  (t/are [expected form]
      (= expected (str form))

    "<div></div>"
    (html [:div])

    "<div></div>"
    #html [:div]

    "<div></div>"
    #xml [:div]

    "<a href=\"#\">Hi</a>"
    (html [:a {:href "#"} "Hi"])

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

    "<ul><li>1</li><li>2</li><li>3</li></ul>"
    (html [:ul [:li 1]
           (map (fn [i]
                  (html [:li i]))
                [2 3])])

    "<div><br></div>"
    (html [:div [:br]])

    "<div><br></br></div>"
    (xml [:div [:br]])

    "<input checked>"
    (html [:input {:checked true}])

    "<input checked=\"true\"></input>"
    (xml [:input {:checked true}])

    "<a href=\"http://dude\"></a>"
    (html [:a {:href (str "http://" "dude")}]))
  )

(comment
  (ok)
  (require '[hiccup2.core :as h])
  (require '[clojure.walk :refer [macroexpand-all]])
  (defn ul []
    (html [:ul [:li 1]
           (map (fn [i]
                  (html [:li i]))
                [2 3])]))
  (macroexpand-all '(html [:ul [:li 1]
                           (map (fn [i]
                                  (html [:li i]))
                                [2 3])]))
  (time (dotimes [_ 10000000] (ul))) ;; ~3600ms

  (defn ul-hiccup []
    (hiccup2.core/html [:ul [:li 1]
                        (map (fn [i]
                               (hiccup2.core/html [:li i]))
                             [2 3])]))
  (time (dotimes [_ 10000000] (ul-hiccup))) ;; ~5500ms
  (macroexpand-all '(html [:a {:href "dude"}]))
  )
