(ns oxcart.pattern
  {:doc "Implements a number of pattern matching predicates and utility
        functions over the clojure.tools.analyzer.jvm AST structure."
   :added "0.0.1"
   :author "Reid McKenzie"}
  (:refer-clojure :exclude [fn?]))


;; TODO:
;;  Depending on benchmarking and API changes it may make sense to
;;  make core.match a dependency of this project rather than hard
;;  coding datastructure dependant paths and equality checks.

(defn with-meta?
  "λ [AST] → Boolean

  Indicates whether the top level form of the argument AST is a metadata form."
  [ast]
  (-> ast
      :op
      (= :with-meta)))

(defn without-meta
  "λ [AST] → AST

  If the argument AST is a metadata form, returns the expression wrapped with
  meta without metadata. Otherwise returns the argument AST."
  [ast]
  (if (with-meta? ast)
    (:expr ast)
    ast))

;; Defs
;;------------------------------------------------------------------------------
(defn def?
  "λ [AST] → Boolean

  Indicates whether the top level form of the argument AST is a def form."
  [ast]
  (-> ast without-meta :op (= :def)))

(defn def->symbol
  "λ [AST] -> (Option Symbol)

  If the argument form was a def, returns the defined symbol. Otherwise the
  return value is garbage."
  [ast]
  (when (def? ast)
    (-> ast without-meta :name)))

(defn def->var
  "λ [AST] → (Option Var)

  If the argument form was a def returns the defined var. Otherwise the return
  value is garbage."
  [ast]
  (when (def? ast)
    (-> ast without-meta :var)))

(defn top-level?
  "λ [AST] → Boolean

  Indicates whether the argument AST is a top level form. Will not eat a
  wrapping :with-meta form."
  [ast]
  (-> ast :top-level))

(defn private?
  "λ [AST] → bool

  Indicates whether the AST node passed as an argument is flagged as
  private. Definitions may be public or private, all other
  values (constant expressions, function applications and soforth) are
  defined to be private."
  [form]
  (if (or (def? form)
          (var? form))
    (-> form :var meta :private true?)
    true))

(defn public?
  "λ [AST] → bool

  Indicates whether the AST node passed as the first argument is
  flagged as public. Definitions are public by default unless marked
  private. All other values (constant expressions, function
  applications and soforth) are defined to be private."
  [form]
  (-> form private? not))

(defn dynamic?
  "λ [AST] → bool

  Indicates whether the AST node passed as the argument is flagged as
  dynamic. Dynamic status is not currently conditional on being a
  definition, however this behavior is subject to change."
  [form]
  (if (or (def? form)
          (var? form))
    (-> form :var meta :dynamic true?)
    false))

(defn const?
  "λ [AST] → bool

  Indicates whether the AST node passed as the argument is flagged as
  const. If the node is both const and dynamic, it is not const. An
  error may be issued if this is ever the case as it represents a
  contradiction in terms."
  [form]
  (case (-> form :meta :form :const)
    (true nil) (not (dynamic? form))
    :else      false))

;; Fns
;;------------------------------------------------------------------------------
(defn fn?
  "λ [AST] → Boolean

  Indicates whether the top level form of the argument AST is a fn."
  [ast]
  (-> ast :op (= :fn)))

(defn fn->name
  "λ [AST] → (Option Symbol)

  If the argument AST is a fn, attempts to return the internal name of
  the fn defaulting to a freshly generated symbol."
  [ast]
  (when (fn? ast)
    (-> ast :internal-name)))

(defn fn-method?
  "λ [AST] → Boolean

  Indicates whether the argument form is a fn-method."

  [ast]
  (-> ast :op (= :fn-method)))

;; Let
;;------------------------------------------------------------------------------
(defn let?
  "λ [AST] → Boolean

  Indicates whether the argument form is a let."
  [ast]
  (-> ast :op (= :let)))

(defn letfn?
  "λ [AST] → Boolean

  Indicates whether the argument form is a letfn."
  [ast]
  (-> ast :op (= :letfn)))

(defn binding?
  "λ [AST] → Boolean

  Indicates whether the argument form is a binding node."
  [ast]
  (-> ast :op (= :binding)))

(defn binding->symbol
  "λ [AST] → (Option Symbol)

  If the argument form was a binding, returns the bound local
  symbol."
  [ast]
  (when (binding? ast)
    (-> ast :name)))

(defn binding->value
  "λ [AST] → (Option AST)

  If the argument form was a binding, returns the bound value as an
  AST node."
  [ast]
  (when (binding? ast)
    (-> ast :init)))

(defn local?
  "λ [AST] → Boolean

  Indicates whether the argument form is a local node."
  [ast]
  (-> ast :op (= :local)))

(defn local->symbol
  "λ [AST] → (Option symbol)

  If the argument AST was a local node, then this operation returns
  the name field otherwise nil."
  [ast]
  (when (local? ast)
    (:name ast)))

;; Invocations
;;------------------------------------------------------------------------------
(defn invoke?
  "λ [AST] → Boolean

  Indicates whether the argument top level form is an invocation."
  [ast]
  (-> ast :op (= :invoke)))

(defn invoke->fn
  "λ [AST] → (Option AST)

  If the argument AST was an invocation, returns the AST representing
  the invoked value."
  [ast]
  (when (invoke? ast)
    (-> ast :fn)))
