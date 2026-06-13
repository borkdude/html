# Changelog

Unreleased changes are available via `io.github.borkdude/html {:git/sha "..."}` in `deps.edn`.

[html](https://github.com/borkdude/html): Produce HTML from hiccup in Clojure and ClojureScript

## 0.2.5

- [#16](https://github.com/borkdude/html/pull/16): only render a map attribute value as CSS when the key is `style`; other map-like values such as records now render via `str` ([@telekid](https://github.com/telekid))
- Fix escaping in cljs when escaped characters appear more than once in string ([@telekid](https://github.com/telekid))

## 0.2.4

- Fix attribute value bound to a symbol: `(let [x "blue"] (html [:div {:color x}]))` now resolves the symbol at runtime instead of emitting its name

## 0.2.3

- Make `escape-html` public ([@kanwei](https://github.com/kanwei))

## 0.2.2

- Fix [#3](https://github.com/borkdude/html/issues/3): allow dynamic attribute value: `(html [:a {:a (+ 1 2 3)}])`
- Fix [#9](https://github.com/borkdude/html/issues/9): shortcuts for id and classes

## 0.1.1

- Fix [#7](https://github.com/borkdude/html/issues/7): boolean HTML attributes

## 0.1.0

Initial release
