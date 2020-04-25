(ns tammymakesthings.core-spec
  (:require [speclj.core :refer :all]
            [tammymakesthings.core :refer :all]))

(describe "tammymakesthings.core"

  (describe "dispatch-arg"
    (it "should be defined"
        (should (fn? dispatch-arg)))
  )
)

(run-specs)
