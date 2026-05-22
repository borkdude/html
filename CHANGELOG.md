# Changelog

Unreleased changes are available via `io.github.borkdude/html {:git/sha "..."}` in `deps.edn`.

[html](https://github.com/borkdude/html): Produce HTML from hiccup in Clojure and ClojureScript

## Unreleased

- Fix symbol-valued attributes being rendered as their symbol name instead of their runtime value: `(let [x 1] (html [:div {:data-x x}]))` now yields `data-x="1"` rather than `data-x="x"`. Extends the dynamic-attribute support from [#3](https://github.com/borkdude/html/issues/3) to bare symbols and symbols nested in literal attribute collections.

## 0.2.3

- Make `escape-html` public ([@kanwei](https://github.com/kanwei))

## 0.2.2

- Fix [#3](https://github.com/borkdude/html/issues/3): allow dynamic attribute value: `(html [:a {:a (+ 1 2 3)}])`
- Fix [#9](https://github.com/borkdude/html/issues/9): shortcuts for id and classes

## 0.1.1

- Fix [#7](https://github.com/borkdude/html/issues/7): boolean HTML attributes

## 0.1.0

Initial release
