;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Versions of the Fibonacci Sequence in Clojure
;;;; Tammy Cravit - tammymakesthings@gmail.com - 2020-05-06
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Permission is hereby granted, free of charge, to any person obtaining a
;;;; copy of this software and associated documentation
;;;; files (the "Software"), to deal in the Software without restriction,
;;;; including without limitation the rights to use, copy, modify, merge,
;;;; publish, distribute, sublicense, and/or sell copies of the Software, and
;;;; to permit persons to whom the Software is furnished to do so, subject to
;;;; the following conditions:
;;;;
;;;; The above copyright notice and this permission notice shall be included
;;;; in all copies or substantial portions of the Software.
;;;;
;;;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
;;;; OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
;;;; MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
;;;; NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
;;;; DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
;;;; OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
;;;; USE OR OTHER DEALINGS IN THE SOFTWARE.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn recursive-fibonacci
    "Recursive implementation of the Fibonacci sequence."
    [n]
    (cond
      (= n 0) 0
      (= n 1) 1
      :else (+ (recursive-fibonacci (- n 1))
               (recursive-fibonacci (- n 2)))))

(defn tail-recursive-fibonacci
    "Tail-recursive version of the fibonacci sequence."
    [n]
    (if (> n 1)
        (loop [x 1 fib0 0 fib1 1]
            (if (< x n)
                (recur [inc x] fib1 (+ fib0 fib1))
                fib1))
    n))

(def lazy-sequence-fibonacci
    "Create a lazy sequence version of the Fibonacci sequence."
    (
     (fn fib [a b] (lazy-seq (cons a (fib b (+ a b)))))
      0 1))

(def lazy-cat-fibonacci
    (lazy-cat [0 1] (map + (rest lazy-cat-fibonacci) lazy-cat-fibonacci)))

(def seq-iterate-fibonacci
    (map first (iterate (fn [[a b]] [b (+ a b)]) [0 1])))

;;;;
;;;; Test Harness for our various implementations
;;;;

; How many terms in the sequence should we collect?
(def num-terms 50)

(defn run-test
  "Run a test of one of our implementations."
  [name impl num-terms]
  (println "=>" name "fibonacci -" num-terms " repetitions:")
  (time (take num-terms impl))
  (println ""))


(println "***********************************************************************")
(println "*          Fibonacci Sequence - Clojure Implementation Tester         *")
(println "***********************************************************************")
(println "")
(run-test "recursive"      recursive-fibonacci      num-terms)
(run-test "tail recursive" tail-recursive-fibonacci num-terms)
(run-test "lazy-seq"       lazy-sequence-fibonacci  num-terms)
(run-test "lazy-cat"       lazy-cat-fibonacci       num-terms)
(run-test "iterate"        seq-iterate-fibonacci    num-terms)

true  ; So we don't return the output of the last test
