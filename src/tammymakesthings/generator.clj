;;;; -*- mode: clojure ; fill-column: 78 -*-
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; tammymakesthings - Static blog generator for tammymakesthings.com
;;;; File         : generator.clj
;;;; Description  : Content generator (posts, pages, projects, etc)
;;;; Last Updated : Time-stamp: <>
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

(defn path-for
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

  ([kind path-extra slug include-date? make-subdir?]
   (path-join
    (content-basedir)
     "content/md"
     (clojure.string/replace (str kind "s") ":" "")
     (if (nil? path-extra) "" (str path-extra))
     (if make-subdir? (str slug) "")
     (if include-date?
      (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd")
                    (new java.util.Date)) "-" slug ".md")
      (str slug ".md")))))

(defn create-new-content!
  "Create a new content item, creating its content subdirectory if needed."
  [content-def]
  (let [filename (path-for content-def)]
    (if (content-def :make-subdir?)
      (io/make-parents filename))
    (with-open [w (io/writer filename )]
      (.write w "{\n")
      (.write w (str "  :title  \"" (str (content-def :title)) "\"\n"))
      (.write w (str "  :layout " (str (content-def :kind)) "\n"))
      (.write w "  :tags   [\"admin\"]")
      (.write w "}\n\n")
      )))

(defn make-page!
  "Create a new page."
  []
  (let [title (helpers/get-user-input "Page title:" "")]
    (if (empty? title)
      (println "No title provided - aborting!")
      (create-new-content!
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
      (create-new-content!
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
      (create-new-content!
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
