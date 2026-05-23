# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`io.github.borkdude/html`: produces HTML from hiccup vectors in Clojure and ClojureScript. The whole library is one `.cljc` namespace (`src/borkdude/html.cljc`, ~100 lines). Hiccup is compiled to string-building code at macroexpansion time; vectors are never walked at runtime. Read `README.md` for the user-facing API and examples.

## Commands

Tasks are defined in `bb.edn`:

- `bb test:clj` - run Clojure tests (`clojure -X:test`, cognitect test-runner)
- `bb test:cljs` - run ClojureScript tests (`clojure -M:test:cljs-test-runner`)
- `bb publish` - deploy to Clojars (`clojure -T:build deploy`)

CI (`.github/workflows/ci.yml`) runs both test suites on push and PR.

Run a single Clojure test var:

```
clojure -X:test :vars '[borkdude.html-test/ok]'
```

Tests live in `test/borkdude/html_test.cljc` and run under both Clojure and ClojureScript (so assertions must hold in both runtimes).

## Versioning and release

Version lives in `deps.edn` under `:aliases :neil :project :version`. Bump it with neil (`neil project set version X.Y.Z`); `build.clj` reads it from there. Update `CHANGELOG.md`, then run `script/changelog.clj` (a babashka script) to expand `#123` issue refs and `@user` mentions into markdown links.

## Architecture

Everything routes through `->html`, a compile-time recursive function that turns a hiccup form into code emitting a `(->Html (str ...))`. The `html` and `xml` macros, and the `html-reader`/`xml-reader` data readers, are all thin wrappers over `->html` (the readers/macros differ only by passing `{:mode :xml}`).

- `Html` deftype wraps a string. Already-rendered children are detected via `instance? Html` and spliced in without re-escaping, which prevents double-encoding. This is why nested sequences of children must each be wrapped in `html` (see README "Child seqs").
- Escaping: string and other literals go through `escape-html`. Only `:$` (unsafe) tag content bypasses escaping.
- Special tags: `:<>` is a fragment (`omit-tag?`, no surrounding element); `:$` is raw/unsafe HTML.
- Attribute compilation (`compile-attrs`): when all attr values are compile-time constants and there's no `:&`, attrs are rendered to a string at compile time. If any value is a non-constant (a `seq?` form) or `:&` is present, it falls back to the runtime `->attrs` call. `:&` is the JSX-style spread: dynamic map merged over the static attrs (static acts as default).
- `parse-tag` extracts `#id` and `.class` shorthand from the tag keyword; `merge-attrs` combines those with the explicit attribute map (class shorthand is prepended to explicit class).
- `void-tags` (br, img, input, etc.) render without a closing tag in HTML mode, but DO get closed in `:xml` mode.
- Output is always HTML5 (or XML in `:xml` mode).

When changing rendering behavior, `macroexpand-all` on `(html ...)` forms (see the `comment` block at the bottom of `html.cljc`) is the quickest way to see the generated code.
