(ns tammymakesthings.generator-spec
  (:require [speclj.core :refer :all]
            [tammymakesthings.generator :refer :all]))

(describe 
  "tammymakesthings.generator"
          
  (describe 
    "content-basedir"
    
    (it "should be defined"
        (should (fn? content-basedir)))

    (it "should return the base directory"
        (should (= (content-basedir) "/home/tammy/blog")))
    )
  
  (describe 
    "path-join"
    
    (it "should be defined"
        (should (fn? path-join)))

    (it "should be able to join a two-component path"
        (should (= 
                  (path-join "/home/tammy" "blog") 
                  "/home/tammy/blog")))

    (it "should be able to join a multi-component path"
        (should (=
                  (path-join "/home/tammy" "blog" "content/foo" "bar.md")
                  "/home/tammy/blog/content/foo/bar.md")))

    (it "should ignore empty strings in path components"
        (should (=
                  (path-join "/home/tammy" "foo" "" "bar")
                  "/home/tammy/foo/bar")))
    )

  (describe 
    "title-to-slug"
    
    (it "should be defined"
        (should (fn? title-to-slug)))

    (it "should correctly render the post slug"
        (should (= (title-to-slug "This is a Test") "this-is-a-test"))
        (should (= (title-to-slug "This is a 12345 Test") "this-is-a-12345-test"))
        )

    (it "should raise an exception for a null title"
        (should-throw NullPointerException (title-to-slug nil)))

    (it "should return an empty slug for an empty title"
        (should (= (title-to-slug "") "")))

    (it "should strip inappropriate characters from the slug"
        (should (= (title-to-slug "This #@ is a test") "this-number-at-is-a-test")))

    (it "should trim whitespace from the slug"
        (should (= (title-to-slug "   This is a Test   ") "this-is-a-test")))
    )

  (describe 
    "slug-or-title"

    (it "should be defined"
        (should (fn? slug-or-title)))
    
    (it "should return the slug if the slug is defined"
        (should (= (slug-or-title "the-slug" "the-title" "default")
                   "the-slug")))

    (it "should return the title if the slug is empty"
        (should (= (slug-or-title "" "the-title" "default")
                   "the-title")))

    (it "should return the title if the slug is nil"
        (should (= (slug-or-title nil "the-title" "default")
                   "the-title")))

    (it "should return the default value if the title and slug are empty"
        (should (= (slug-or-title "" "" "default")
                   "default")))

    (it "should return the default value if the title and slug are nil"
        (should (= (slug-or-title nil nil "default")
                   "default")))    
    )

  (describe "path-for"
    (it "should be defined"
      (should (fn? path-for)))
    )

  (describe "maybe-make-tags-array"
    (it "should be defined"
      (should (fn? maybe-make-tags-array)))
    )

  (describe "make-empty-file-contents"
    (it "should be defined"
      (should (fn? make-empty-file-contents)))
    )

  (describe "make-content-item!"
    (it "should be defined"
      (should (fn? make-content-item!)))
    )

  (describe "make-page!"
    (it "should be defined"
      (should (fn? make-page!)))
    )

  (describe "make-post!"
    (it "should be defined"
      (should (fn? make-post!)))
    )

  (describe "make-project!"
    (it "should be defined"
      (should (fn? make-project!)))
    )

  (describe "build-site!"
    (it "should be defined"
      (should (fn? build-site!)))
  )
)

(run-specs)
