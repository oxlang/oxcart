;;   Copyright (c) Reid McKenzie, Rich Hickey & contributors. The use
;;   and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0
;;   (http://opensource.org/licenses/eclipse-1.0.php) which can be
;;   found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be
;;   bound by the terms of this license.  You must not remove this
;;   notice, or any other, from this software.

(ns oxcart.pattern
  {:doc "Implements a number of pattern matching predicates and utility
        functions over the clojure.tools.analyzer.jvm AST structure."
   :added "0.0.1"
   :author "Reid McKenzie"})


;; TODO:
;;  Depending on benchmarking and API changes it may make sense to
;;  make core.match a dependency of this project rather than hard
;;  coding datastructure dependant paths and equality checks.

(defn def?
  "λ AST -> Boolean

  Indicates whether the top level form of the argument AST is a def form."
  [ast]
  (-> ast
      :op
      (= :def)))


(defn def->symbol
  "λ AST -> (Option Symbol)

  If the argument form was a def, returns the defined
  symbol. Otherwise the return value is garbage."
  [ast]
  (when (def? ast)
    (:name ast)))


(defn private?
  "λ AST → bool

  Indicates whether the AST node passed as an argument is flagged as
  private. Definitions may be public or private, all other
  values (constant expressions, function applications and soforth) are
  defined to be private."
  [form]
  (let [status (-> form :meta :form :private)]
    (if (def? form)
      (true? status)
      true)))


(defn public?
  "λ AST → bool

  Indicates whether the AST node passed as the first argument is
  flagged as public. Definitions are public by default unless marked
  private. All other values (constant expressions, function
  applications and soforth) are defined to be private."
  [form]
  (-> form private? not))


(defn dynamic?
  "λ AST → bool

  Indicates whether the AST node passed as the argument is flagged as
  dynamic. Dynamic status is not currently conditional on being a
  definition, however this behavior is subject to change."
  [form]
  (-> form :meta :form :dynamic true?))


(defn const?
  "λ AST → bool

  Indicates whether the AST node passed as the argument is flagged as
  const. If the node is both const and dynamic, it is not const. An
  error may be issued if this is ever the case as it represents a
  contradiction in terms."
  [form]
  (case (-> form :meta :form :const)
    (true nil) (not (dynamic? form))
    :else      false))
