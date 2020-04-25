;;;; -*- mode: clojure ; fill-column: 78 -*-
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; tammymakesthings - Static blog generator for tammymakesthings.com
;;;; File         : generator.clj
;;;; Description  : Content generator (posts, pages, projects, etc)
;;;; Last Updated : Time-stamp: <2020-04-25 19:15:53 tammy>
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
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [cryogen-core.plugins :refer [load-plugins]])
  (:gen-class))

(use 'debux.core)
(use 'slugger.core)

(defn path-join
  "Join path components into a string path."
  [p & ps]
  (str (.normalize (java.nio.file.Paths/get p (into-array String ps)))))

(defn content-basedir
  "Get the base directory for the content."
  []
  (path-join (System/getProperty "user.home") "projects" "blog" "content" "md"))

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

  ([{:keys [kind path-extra slug title include-date? make-subdir?] :as content-def}]
   (path-for kind
             path-extra
             (slug-or-title slug title "default-slug")
             include-date?
             make-subdir?))

  ([kind slug]
   (path-for kind "" slug false false))

  ([kind path-extra slug]
   (path-for kind path-extra slug false false))

  ([kind path-extra slug include-date? make-subdir?]
   (path-join
    (content-basedir)
     (clojure.string/replace (str kind "s") ":" "")
     (if (> (count path-extra) 0) (str path-extra) "")
     (if make-subdir? (str slug) "")
     (if include-date?
      (str (.format (java.text.SimpleDateFormat. "yyyy-MM-dd")
                    (new java.util.Date)) "-" slug ".md")
      (str slug ".md")))))

(defn maybe-make-tags-array
  "Convert an array of tags to a string (Markdown) representation."
  [tags]
  (if (or (nil? tags) (= (count tags) 0))
    ""
    (str
       "  :tags   ["
       (clojure.string/join " " (map #(str "\"" % "\"") tags))
       "]\n"
    )))

(defn make-empty-file-contents
  "Build the contents of the empty file and return it as a string."
  [{:keys [:title :kind :tags] :as content-def}]
  (str "{\n"
       "  :title  \"" (if (nil? title) "Default Title" title) "\"\n"
       "  :layout "   (if (nil? kind) ":post" (str kind)) "\n"
       (maybe-make-tags-array (content-def :tags))
       "}\n\n"))

(defn make-content-item!
  "Create a new content item, creating its content subdirectory if needed."
  [content-def]
  (let [
        filename      (path-for content-def)
        file-contents (make-empty-file-contents content-def)
       ]
    (if (content-def :make-subdir?)
      (io/make-parents filename))
    (spit filename file-contents)))

(defn build-site!
  []
  (compile-assets-timed))
