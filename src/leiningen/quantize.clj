(ns leiningen.quantize
  (:require [leiningen.core.eval :as eval]))

(defn quantize
  "I don't do a lot."
  [project & args]
  (eval/eval-in-project '{:dependencies [[org.clojure/clojure "1.5.0-beta1"]
                                         [org.cloudhoist/codeq "0.1.0-SNAPSHOT"]
                                         [quantize "0.1.0-SNAPSHOT"]]}
                        `(quantize.core/run '~project ~@args)
                        '(require 'quantize.core)))


