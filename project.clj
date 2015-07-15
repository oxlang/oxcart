(defproject me.arrdem/oxcart (slurp "VERSION")
  :description "An experimental optimizing compiler for Clojure code"
  :url "http://github.com/arrdem/oxcart"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure            "1.6.0"]
                 [org.clojure/tools.analyzer     "0.6.6"]
                 [org.clojure/tools.analyzer.jvm "0.6.7"]
                 [org.clojure/tools.emitter.jvm  "0.1.0-SNAPSHOT"]
                 [org.clojure/tools.reader       "0.9.2"]
                 [com.taoensso/timbre            "4.0.2"]
                 ]
  :main oxcart.core
  :min-lein-version "2.0.0"
  :source-paths ["src/main/clojure"
                 "src/bench/clojure"]
  :test-paths   ["src/test/clojure"]
  :java-source-paths ["src/main/java"]
  :whitelist #"oxcart"
  :injections [(set! *print-length* 10)
               (set! *print-level*  10)])
