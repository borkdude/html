# html

Produce HTML from Clojure and ClojureScript.

## Rationale

[Squint](https://github.com/squint-cljs/squint) and
[cherry](https://github.com/squint-cljs/cherry) support HTML generation as
built-in functionality. Some people wanted this functionality in JVM Clojure and
ClojureScript as well. That is what this library offers.

Benefits over some (but definitely not all) hiccup libraries may be:

- Generation of HTML is done at compile time, if possible
- The generated code is small and easy to understand
- The library itself is small
- The library works both in Clojure and ClojureScript

Drawbacks of this library:

- New and thus not as mature and battle tested as other libraries. Issues + PRs welcome though.

In this README, all example results are written as strings. In reality they are
a `borkdude.html.Html` object which just contains a string. This is done to
prevent issues with double-encoding.

## Examples

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

## Passing props

This library doesn't support dynamic creation of attributes in the same way that
some hiccup libraries do. Rather, you have to use the special `:&` property to
pass any dynamic properties, reminiscent of the JSX spread operator:

``` clojure
(let [m {:style {:color :blue} :class "foo"}]
  (html [:div {:class "bar" :& m}]))
;;=> "<div class=\"foo\" style=\"color: blue;\"></div>"
```

Any static properties, like `:class "bar"` above function as a default which
will be overridden by the dynamic map `m`.

## Fragment

A fragment can be written in a similar way as JSX with `:<>` as the tag:

``` clojure
(html [:div [:<> "Hello " "world"]]) ;;=> <div>Hello world</div>
```

## Unsafe

Unsafe HTML (which won't be HTML-escaped) can be written with:

``` clojure
(html [:$ "<whatever>]) ;;=> "<whatever>"
```

## Child components

Just use function calls for child components:

``` clojure
(defn child-component [{:keys [name]}]
  (html [:div "Hello " name]))

(defn App []
  (html
   [:div
    [:div {:color :blue}]
    (child-component {:name "Michiel"})]))

(App) ;=> "<div><div color=\"blue\"></div><div>Hello Michiel</div></div>"
```

## Child seqs

To render a sequence of child elements, use `html` to render the child element as well:

``` clojure
(html
  [:ul
    [:li 1]
    (map (fn [i] (html [:li i])) [2 3])])
;;=> "<ul><li>1</li><li>2</li><li>3</li></ul>"
```

## Performance

Despite the relative simplicity of this library, performance is quite good. Here is an informal benchmark against `hiccup/hiccup`:

``` clojure
(comment
  (defn ul []
    (html [:ul [:li 1]
           (map (fn [i]
                  (html [:li i]))
                [2 3])]))
  (time (dotimes [_ 10000000] (ul))) ;; ~3600ms

  (defn ul-hiccup []
    (hiccup2.core/html [:ul [:li 1]
                        (map (fn [i]
                               [:li i])
                             [2 3])]))
  (time (dotimes [_ 10000000] (ul-hiccup))) ;; ~5500ms
  )
```

## Data reader

To install the `#html` reader, add the following to `data_readers.cljc`:

``` clojure
{html borkdude.html/reader}
```

Then you can write:

``` clojure
#html [:div "Hello"]
```

like you can in squint.

Note that this data reader isn't enabled by default since it's not recommended
to use unqualified data readers for libraries since this can be a source of
conflicts.
