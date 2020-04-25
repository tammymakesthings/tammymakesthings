(ns tammymakesthings.helpers-spec
  (:require [speclj.core :refer :all]
            [tammymakesthings.helpers :refer :all]))

(describe 
  "tammymakesthings.helpers"
  
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
