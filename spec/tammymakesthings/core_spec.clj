(ns tammymakesthings.core-spec
  (:require [speclj.core :refer :all]
            [tammymakesthings.core :refer :all]))

(describe "Tammy Makes Things - Cryogen Core"

  (it "is true"
    (should true))

  (it "is not false"
    (should-not false)))

(run-specs)
