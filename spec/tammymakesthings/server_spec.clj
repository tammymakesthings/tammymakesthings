(ns tammymakesthings.server-spec
  (:require [speclj.core :refer :all]
            [tammymakesthings.server :refer :all]))

;;
;; Heloers to test function arity
;;

(defn provided
  [cond fun x]
  (if cond
    (fun x)
    x))

(defn append
  [xs x]
  (conj (vec xs) x))

(defn arity-of-method
  [method]
  (->> method .getParameterTypes alength))

(defn arities-of
  [fun]
  (let [all-declared-methods (.getDeclaredMethods (class fun))
        methods-named (fn [name]
                        (filter #(= (.getName %) name) all-declared-methods))
        methods-named-invoke (methods-named "invoke")
        methods-named-do-invoke (methods-named "doInvoke")
        is-rest-fn (seq methods-named-do-invoke)]
    (->> methods-named-invoke
         (map arity-of-method)
         sort
         (provided is-rest-fn
                   (fn [v] (append v :rest))))))

(defn have-arity?
  "Check if a form responds to the specified arity."
  [fun arity]
  (.contains (arities-of fun) arity))

(describe
  "tammymakesthings.server"

  (context 
    "init"

    (it "should exist"
        (should (fn? init)))

    (it "should have an arity of 0"
        (should (have-arity? init 0)))
    )

  (context 
    "wrap-subdirectories"

    (it "should exist"
        (should (fn? wrap-subdirectories)))

    (it "should have an arity of 1"
        (should (have-arity? wrap-subdirectories 1)))
    )
)

(run-specs)
