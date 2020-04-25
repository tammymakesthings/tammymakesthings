(ns tammymakesthings.cli-spec
  (:require [speclj.core :refer :all]
            [tammymakesthings.cli :refer :all]))

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
  "tammymakesthings.cli"

  (context 
    "dispatch-arg"

    (it "should be defined"
        (should (fn? dispatch-arg)))
    
    (it "should have an arity of 1"
        (should (have-arity? dispatch-arg 1)))
    )
  
  (context
    "get-user-input"

    (it "should be defined"
        (should (fn? get-user-input)))

    (it "should be invokeable with a prompt and a default"
        (should (have-arity? get-user-input 2)))

    (it "should be invokeable with just a prompt"
        (should (have-arity? get-user-input 1)))

    (it "should be invokeable with no arguments"
        (should (have-arity? get-user-input 0)))
    )
  
  (context
    "prompt-y-or-n"
    
    (it "should be defined"
        (should (fn? prompt-y-or-n)))

    (it "should have an arity of 1"
        (should (have-arity? prompt-y-or-n 1)))
    )

  (context
    "display-version!"

    (it "should be defined"
        (should (fn? display-version!)))

    (it "should have an arity of 0"
        (should (have-arity? display-version! 0)))
    )

  (context
    "app-banner"

    (it "should be defined"
        (should (fn? app-banner)))

    (it "should have an arity of 0"
        (should (have-arity? app-banner 0)))
    )

  (context
    "display-help!"

    (it "should be defined"
        (should (fn? display-help!)))

    (it "should have an arity of 0"
        (should (have-arity? display-help! 0)))
    )

  (context
    "make-post!"

    (it "should be defined"
        (should (fn? make-post!)))

    (it "should be invokeable with a title and subdir flag"
        (should (have-arity? make-post! 2)))

    (it "should be invokeable with no arguments"
        (should (have-arity? make-post! 0)))
    )

  (context
    "make-page!"

    (it "should be defined"
        (should (fn? make-page!)))

    (it "should be invokeable with a title and subdir flag"
        (should (have-arity? make-page! 2)))

    (it "should be invokeable with no arguments"
        (should (have-arity? make-page! 0)))
    )

  (context
    "make-project!"
    
    (it "should be defined"
        (should (fn? make-project!)))

    (it "should be invokeable with a title"
        (should (have-arity? make-project! 1)))

    (it "should not be invokeable with a title and subdir flag"
        (should-not (have-arity? make-project! 2)))

    (it "should be invokeable with no arguments"
        (should (have-arity? make-project! 0)))
    )
)

(run-specs)
