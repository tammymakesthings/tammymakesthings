;;;; -*- mode: clojure ; fill-column: 78 -*-
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; tammymakesthings - Static blog generator for tammymakesthings.com
;;;; File         : generator.clj
;;;; Description  : Content generator (posts, pages, projects, etc)
;;;; Last Updated : Time-stamp: <2020-04-24 17:45:05 tammy>
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; Based on the cryogen static site builder
;;;; (github - cryogen-project/cryogen)
;;;;
;;;; Extended by Tammy Cravit <tammymakesthings@gmail.com> with site admin
;;;; and other tweaks.
;;;;
;;;; Source : github - tammymakesthings/tammymakesthings
;;;; License: Eclipse Public License, eclipse.org/legal/epl-v10.html
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns tammymakesthings.generator
  (:require [cryogen-core.compiler :refer [compile-assets-timed]]
            [clojure.java.io :as io]
            [tammymakesthings.helpers :as helpers]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [cryogen-core.plugins :refer [load-plugins]])
  (:gen-class))

(use 'debux.core)
(use 'slugger.core)

(defn content-basedir
  "Get the base directory for the content."
  []
  "/home/tammy/blog"
)

(defn path-join
  "Join path components into a string path."
  [p & ps]
  (str (.normalize (java.nio.file.Paths/get p (into-array String ps)))))

(defn title-to-slug
  "Convert a post title to a slug."
  [title]
  (->slug (clojure.string/trim title)))

(defn slug-or-title
  "Make the slug for a post from the content-def."

  (
   [slug title]
   (slug-or-title slug title "default-content")
  )

  (
   [slug title default]
   (if (> (count slug) 0)
     slug
     (if (> (count title) 0)
       (title-to-slug title)
       default))
   ))

(dbgn (defn path-for
  "Get the path for a content file."

  ([content-def]
     (path-for
       (content-def :kind)
       (content-def :path-extra)
       (slug-or-title (str (content-def :slug))
                      (str (content-def :title))
                      "default-slug")
       (content-def :include-date?)
       (content-def :make-subdir?)))

  ([kind slug]
   (path-for kind "" slug false false))

  ([kind path-extra slug]
   (path-for kind path-extra slug false false))

  ([kind path-extra slug include-date make-subdir]
   (path-join
    (content-basedir)
     "content/md"
     (clojure.string/replace (str kind "s") ":" "")
     (if (or (nil? path-extra) (= (count path-extra) 0))
       ""
       (str path-extra))
     (if make-subdir
       (str slug)
       "")
     (if include-date
      (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd")
                    (new java.util.Date)) "-" slug ".md")
      (str slug ".md"))))))

(defn maybe-make-tags-array
  "Convert an array of tags to a string (Markdown) representation."
  [tags]
  (if (or (nil? tags) (= (count tags) 0))
    ""
    (str
       "  :tags   ["
       (clojure.string/join ", " (map #(str "\"" % "\"") tags))
       "]\n"
    )))

(dbgn (defn make-empty-file-contents
  "Build the contents of the empty file and return it as a string."
  [content-def]
  (str "{\n"
       "  :title  \"" (content-def :title) "\n"
       "  :layout "   (content-def :kind) "\n"
       (maybe-make-tags-array (content-def :tags))
       "}\n\n")))

(dbgn (defn make-content-item!
  "Create a new content item, creating its content subdirectory if needed."
  [content-def]
  (let [
        filename      (path-for content-def)
        file-contents (make-empty-file-contents content-def)
       ]
    (if (content-def :make-subdir?)
      (io/make-parents filename))
    (spit filename file-contents))))

(defn make-page!
  "Create a new page."
  []
  (let [title (helpers/get-user-input "Page title:" "")]
    (if (empty? title)
      (println "No title provided - aborting!")
      (make-content-item!
       {
        :title         title
        :slug          (title-to-slug title)
        :kind          :page
        :include-date? false
        :make-subdir?  (helpers/prompt-y-or-n "Create directory? ")
        }
       ))))

(defn make-post!
  "Create a new blog post."
  []
  (let [title (helpers/get-user-input "Page title:" "")]
    (if (empty? title)
      (println "No title provided - aborting!")
      (make-content-item!
       {
        :title         title
        :slug          (title-to-slug title)
        :kind          :post
        :include-date? true
        :make-subdir?  false
        }
       ))))

(defn make-project!
  "Create a new project under pages/projects."
  []
  (let [title (helpers/get-user-input "Project title:" "")]
    (if (empty? title)
      (println "No title provided - aborting!")
      (make-content-item!
       {
        :title         title
        :slug          (title-to-slug title)
        :kind          :page
        :path_extra    "projects"
        :include-date? false
        :make-subdir?  true
        }
       ))))

(defn build-site!
  []
  (compile-assets-timed))
