(ns tammymakesthings.generator-spec
  (:require [speclj.core :refer :all]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [tammymakesthings.generator :refer :all]))

(require '[clojure.java.io :as io] )
(class (io/as-file "."))

;;
;; Helpers for testing content generation
;;

(defn exist-in-filesystem?
  "Check if a file exists."
  [fname]
  (.exists (clojure.java.io/file fname)))

(defn be-non-empty?
  "Check if a file has a non-zero size."
  [fname]
  (> (.length (clojure.java.io/file fname)) 0))

(defn delete-recursively
  "Delete a file or directory recursively if it exists."
  [fname]
  (when (exist-in-filesystem? fname)
    (let [func (fn [func f]
                 (when (.isDirectory f)
                   (doseq [f2 (.listFiles f)]
                     (func func f2)))
                 (clojure.java.io/delete-file f))]
      (func func (clojure.java.io/file fname)))))

;;
;;  tammymakesthings.generator tests
;;

(describe
  "tammymakesthings.generator"

  (context
    "content-basedir"

    (it "should be defined"
        (should (fn? content-basedir)))

    (it "should return the content base directory"
        (should (= (content-basedir)
                   (path-join (System/getProperty "user.home") "projects" "blog" "content" "md"))))

  (context
    "path-join"

    (it "should be defined"
        (should (fn? path-join)))

    (it "should be able to join a two-component path"
        (should (=
                  (path-join "/home/tammy" "blog")
                  "/home/tammy/blog")))

    (it "should be able to join a multi-component path"
        (should (=
                  (path-join "/home/tammy/" "blog" "content/foo" "bar.md")
                  "/home/tammy/blog/content/foo/bar.md")))

    (it "should ignore empty strings in path components"
        (should (=
                  (path-join "/home/tammy" "foo" "" "bar")
                  "/home/tammy/foo/bar")))
    )

  (context
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

  (context
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

  (context
    "path-for"

    (it "should be defined"
        (should (fn? path-for)))

    (it "should generate the right path given a kind and slug"
        (should (= (path-for :post "test-post")
                   (path-join (content-basedir) "posts" "test-post.md")))
        (should (= (path-for :page "test-page")
                   (path-join (content-basedir) "pages" "test-page.md"))))

    (it "should generate the right path given a kind, slug, and path-extra"
        (should (= (path-for :post "projects" "test-post")
                   (path-join (content-basedir) "posts" "projects" "test-post.md")))
        (should (= (path-for :page "projects" "test-page")
                   (path-join (content-basedir) "pages" "projects" "test-page.md"))))

    (it "should generate the right path with include-date?"
        (let [date-stamp (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))]
          (should (= (path-for :post "" "test-post" true false)
                     (path-join (content-basedir)
                                "posts"
                                (str date-stamp "-test-post.md"))))
          (should (= (path-for :page "" "test-post" true false)
                     (path-join (content-basedir)
                                "pages"
                                (str  date-stamp "-test-post.md"))))))

    (it "should generate the right path with include-date? and path-extra"
        (let [date-stamp (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))]
          (should (= (path-for :post "extra" "test-post" true false)
                     (path-join (content-basedir)
                                "posts" "extra"
                                (str date-stamp "-test-post.md"))))
          (should (= (path-for :page "extra" "test-post" true false)
                     (path-join (content-basedir)
                                "pages" "extra"
                                (str date-stamp "-test-post.md"))))))

    (it "should generate the right path with make-subdir?"
        (should (= (path-for :post "" "test-post" false true)
                   (path-join (content-basedir)
                              "posts" "test-post"
                              "test-post.md")))

        (should (= (path-for :page "" "test-page" false true)
                   (path-join (content-basedir)
                              "pages" "test-page"
                              "test-page.md"))))

    (it "should generate the right path with make-subdir? and path-extra"
        (should (= (path-for :post "extra" "test-post" false true)
                   (path-join (content-basedir)
                              "posts" "extra" "test-post" "test-post.md")))
        (should (= (path-for :page "extra" "test-page" false true)
                   (path-join (content-basedir)
                              "pages" "extra" "test-page" "test-page.md"))))

    (it "should generate the right path with include-date? and make-subdir?"
        (let [date-stamp (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))]
          (should (= (path-for :post "" "test-post" true true)
                     (path-join (content-basedir)
                                "posts" "test-post"
                                (str date-stamp "-test-post.md"))))
          (should (= (path-for :page "" "test-page" true true)
                     (path-join (content-basedir)
                                "pages" "test-page"
                                (str date-stamp "-test-page.md"))))))

    (it "should generate the right path with all options specified"
        (let [date-stamp (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))]
          (should (= (path-for :post "extra" "test-post" true true)
                     (path-join (content-basedir)
                                "posts" "extra" "test-post"
                                (str date-stamp "-test-post.md"))))
          (should (= (path-for :page "extra" "test-page" true true)
                     (path-join (content-basedir)
                                "pages" "extra" "test-page"
                                (str date-stamp "-test-page.md"))))))

    (it "should destructure parameters from a map and make the right path"
        (let [date-stamp (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))
             post-with-slug {:kind :post :path-extra "extra" :slug "test-post"
                              :include-date? true :make-subdir? true}
             post-with-title {:kind :post :path-extra "extra" :title "Test Post"
                              :include-date? true :make-subdir? true}
             page-with-slug {:kind :page :path-extra "extra" :slug "test-page"
                              :include-date? true :make-subdir? true}
             page-with-title {:kind :page :path-extra "extra" :slug "test-page"
                              :include-date? true :make-subdir? true}]

          (should (= (path-for post-with-slug)
                     (path-join (content-basedir)
                                "posts" "extra" "test-post"
                                (str date-stamp "-test-post.md"))))
          (should (= (path-for post-with-title)
                     (path-join (content-basedir)
                                "posts" "extra" "test-post"
                                (str date-stamp "-test-post.md"))))
          (should (= (path-for page-with-slug)
                     (path-join (content-basedir)
                                "pages" "extra" "test-page"
                                (str date-stamp "-test-page.md"))))
          (should (= (path-for page-with-title)
                     (path-join (content-basedir)
                                "pages" "extra" "test-page"
                                (str date-stamp "-test-page.md"))))))
   )

  (context
    "maybe-make-tags-array"

    (it "should be defined"
        (should (fn? maybe-make-tags-array)))

    (it "should return an empty string for a null tags array"
        (should-not-throw (maybe-make-tags-array nil))
        (should (= (maybe-make-tags-array nil) ""))
    )

    (it "should return an empty string for an empty tags array"
        (should (= (maybe-make-tags-array "") ""))
    )

    (it "should return a tags array string when tags are provided"
        (should (= (maybe-make-tags-array ["foo" "bar"])
                   "  :tags   [\"foo\" \"bar\"]\n")))
    )

  (context
    "make-empty-file-contents"

    (it "should be defined"
        (should (fn? make-empty-file-contents)))

    (it "should generate the right content without tags"
        (should (= (make-empty-file-contents {:title "Test File" :kind :post})
                   "{\n  :title  \"Test File\"\n  :layout :post\n}\n\n")))

    (it "should generate the right content with tags"
        (should (= (make-empty-file-contents {:title "Test File" :kind :post :tags ["a" "b"]})
                   "{\n  :title  \"Test File\"\n  :layout :post\n  :tags   [\"a\" \"b\"]\n}\n\n")))

    (it "should use the default title if not provided"
        (should (= (make-empty-file-contents {:title nil :kind :post :tags ["a" "b"]})
                   "{\n  :title  \"Default Title\"\n  :layout :post\n  :tags   [\"a\" \"b\"]\n}\n\n")))
    )



(context
    "make-content-item!"

    (declare ^:dynamic *content-def-file*)
    (declare ^:dynamic *content-def-subdir*)

    (around [it]
            (binding [*content-def-file* {
                                          :kind :post
                                          :path-extra ""
                                          :slug "make-content-item-test"
                                          :make-subdir? false
                                          :include-date? false
                                         }
                      *content-def-subdir* {
                                            :kind :post
                                            :path-extra ""
                                            :slug "make-content-item-test"
                                            :make-subdir? true
                                            :include-date? false
                                            }]
              (delete-recursively (path-for *content-def-file*))
              (delete-recursively (path-for *content-def-subdir*))
              (it)
              (delete-recursively (path-for *content-def-file*))
              (delete-recursively (path-for *content-def-subdir*))
              ))

      (it "should be defined"
          (should (fn? make-content-item!)))

      (it "should generate the new content item in the filesystem"
          (make-content-item! *content-def-file*)
          (should (exist-in-filesystem? (path-for *content-def-file*)))
          (should (be-non-empty? (path-for *content-def-file*)))
          (should (= (make-empty-file-contents *content-def-file*) (slurp (path-for *content-def-file*)))))
      (it "should generate the new content item in a subdir if requested"
          (make-content-item! *content-def-subdir*)
          (should (exist-in-filesystem? (path-for *content-def-subdir*)))
          (should (be-non-empty? (path-for *content-def-subdir*)))
          (should (= (make-empty-file-contents *content-def-subdir*) (slurp (path-for *content-def-subdir*)))))
      )

  (context
    "build-site!"

    (it "should be defined"
      (should (fn? build-site!)))
  )))

(run-specs)
