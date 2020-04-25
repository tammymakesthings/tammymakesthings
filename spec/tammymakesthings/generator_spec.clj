(ns tammymakesthings.generator-spec
  (:require [speclj.core :refer :all]
            [tammymakesthings.generator :refer :all]))

(describe 
  "tammymakesthings.generator"
          
  (context 
    "content-basedir"
    
    (it "should be defined"
        (should (fn? content-basedir)))

    (it "should return the base directory"
        (should (= (content-basedir) "/home/tammy/blog")))
    )
  
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
                  (path-join "/home/tammy" "blog" "content/foo" "bar.md")
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
                   "/home/tammy/blog/content/md/posts/test-post.md"))
        (should (= (path-for :page "test-page")
                   "/home/tammy/blog/content/md/pages/test-page.md")))

    (it "should generate the right path given a kind, slug, and path-extra"
        (should (= (path-for :post "projects" "test-post")
                   "/home/tammy/blog/content/md/posts/projects/test-post.md"))
        (should (= (path-for :page "projects" "test-page")
                   "/home/tammy/blog/content/md/pages/projects/test-page.md")))

    (it "should generate the right path with include-date?"
        (let [date-stamp (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))]
          (should (= (path-for :post "" "test-post" true false)
                     (str "/home/tammy/blog/content/md/posts/" date-stamp "-test-post.md")))
          (should (= (path-for :page "" "test-post" true false)
                     (str "/home/tammy/blog/content/md/pages/" date-stamp "-test-post.md")))
          ))

    (it "should generate the right path with include-date? and path-extra"
        (let [date-stamp (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))]
          (should (= (path-for :post "extra" "test-post" true false)
                     (str "/home/tammy/blog/content/md/posts/extra/" date-stamp "-test-post.md")))
          (should (= (path-for :page "extra" "test-post" true false)
                     (str "/home/tammy/blog/content/md/pages/extra/" date-stamp "-test-post.md")))
          ))

    (it "should generate the right path with make-subdir?"
        (should (= (path-for :post "" "test-post" false true)
                   "/home/tammy/blog/content/md/posts/test-post/test-post.md"))
        (should (= (path-for :page "" "test-page" false true)
                   "/home/tammy/blog/content/md/pages/test-page/test-page.md")))

    (it "should generate the right path with make-subdir? and path-extra"
        (should (= (path-for :post "extra" "test-post" false true)
                   "/home/tammy/blog/content/md/posts/extra/test-post/test-post.md"))
        (should (= (path-for :page "extra" "test-page" false true)
                   "/home/tammy/blog/content/md/pages/extra/test-page/test-page.md")))

    (it "should generate the right path with include-date? and make-subdir?"
        (let [date-stamp (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))]
          (should (= (path-for :post "" "test-post" true true)
                     (str "/home/tammy/blog/content/md/posts/test-post/" date-stamp "-test-post.md")))
          (should (= (path-for :page "" "test-page" true true)
                     (str "/home/tammy/blog/content/md/pages/test-page/" date-stamp "-test-page.md")))))

    (it "should generate the right path with all options specified"
        (let [date-stamp (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (new java.util.Date)))]
          (should (= (path-for :post "extra" "test-post" true true)
                     (str "/home/tammy/blog/content/md/posts/extra/test-post/" date-stamp "-test-post.md")))
          (should (= (path-for :page "extra" "test-page" true true)
                     (str "/home/tammy/blog/content/md/pages/extra/test-page/" date-stamp "-test-page.md")))))

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
                     (str "/home/tammy/blog/content/md/posts/extra/test-post/" date-stamp "-test-post.md")))
          (should (= (path-for post-with-title)
                     (str "/home/tammy/blog/content/md/posts/extra/test-post/" date-stamp "-test-post.md")))
          (should (= (path-for page-with-slug)
                     (str "/home/tammy/blog/content/md/pages/extra/test-page/" date-stamp "-test-page.md")))
          (should (= (path-for page-with-title)
                     (str "/home/tammy/blog/content/md/pages/extra/test-page/" date-stamp "-test-page.md")))))
    )

  (context "maybe-make-tags-array"
    (it "should be defined"
      (should (fn? maybe-make-tags-array)))
    )

  (context "make-empty-file-contents"
    (it "should be defined"
      (should (fn? make-empty-file-contents)))
    )

  (context "make-content-item!"
    (it "should be defined"
      (should (fn? make-content-item!)))
    )

  (context "make-page!"
    (it "should be defined"
      (should (fn? make-page!)))
    )

  (context "make-post!"
    (it "should be defined"
      (should (fn? make-post!)))
    )

  (context "make-project!"
    (it "should be defined"
      (should (fn? make-project!)))
    )

  (context "build-site!"
    (it "should be defined"
      (should (fn? build-site!)))
  )
)

(run-specs)
