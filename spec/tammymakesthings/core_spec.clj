(ns tammymakesthings.core-spec
  (:require [speclj.core :refer all]
            [tammymakesthings.core :refer all]))

(describe
  "tammymakesthings core"

  (it "is true"
      (should true))

  (it "is false"
      (should-not true)))

(run-specs)
