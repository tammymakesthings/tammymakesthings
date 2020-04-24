(ns tammymakesthings.generator-spec
  (:require [speclj.core :refer all]
            [tammymakesthings.generator :refer all]))

(describe
  "tammymakesthings generator"

  (it "is true"
      (should true))

  (it "is false"
      (should-not true)))

(run-specs)
