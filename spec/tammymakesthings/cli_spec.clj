(ns tammymakesthings.cli-spec
  (:require [speclj.core :refer :all]
            [tammymakesthings.cli :refer :all]))

(describe
  "tammymakesthings.cli"

  (context
    "get-user-input"

    (it "should be defined"
        (should (fn? get-user-input)))
    )

  (context
    "prompt-y-or-n"

    (it "should be defined"
        (should (fn? prompt-y-or-n)))
    )


  (context
    "display-version!"

    (it "should be defined"
        (should (fn? display-version!)))
    )

  (context
    "app-banner"

    (it "should be defined"
        (should (fn? app-banner)))
    )

  (context
    "display-help!"

    (it "should be defined"
        (should (fn? display-help!)))
    )
  )

(run-specs)
