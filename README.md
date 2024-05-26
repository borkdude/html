# html

Example:

``` clojure
(require '[borkdude.html :refer [html]])

(let [name "Michiel"]
  (html [:div {:color :blue :style {:color :blue}}
         [:p "Hello there " name
          [:ul
           [:li 1]
           (map (fn [i]
                  (html [:li i]))
                [2 3 4])]]]))
;;=>
"<div color=\"blue\" style=\"color: blue;\"><p>Hello there Michiel<ul><li>1</li><li>2</li><li>3</li><li>4</li></ul></p></div>"
```
