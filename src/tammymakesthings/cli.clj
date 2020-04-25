;;;; -*- mode: clojure ; fill-column: 78 -*-
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; tammymakesthings - Static blog generator for tammymakesthings.com
;;;; File         : cli.clj
;;;; Description  : CLI helper functions.
;;;; Last Updated : Time-stamp: <2020-04-25 20:28:04 tammy>
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

(ns tammymakesthings.cli
  (:require [cryogen-core.compiler :refer [compile-assets-timed]]
            [clojure.java.io :as io]
            [tammymakesthings.generator :as gen]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [cryogen-core.plugins :refer [load-plugins]])
  (:gen-class))

(use 'debux.core)
(use 'slugger.core)

(defn get-user-input
  "Prompts the user to enter a string."
  (
   []
   (get-user-input "Your input")
  )
  (
   [prompt]
   (get-user-input prompt "")
  )
  (
   [prompt default]
   (print prompt)
   (flush)
   (let [v (read-line)]
     (if (> (count v) 0)
       (clojure.string/trim v)
       default))))

(defn prompt-y-or-n
  "Prompt the user for a yes or no input."
  [prompt]
  (let [input (get-user-input prompt "no")]
    (clojure.string/starts-with? input "y")))

(defn make-post!
  "Prompts the user for necessary information and makes a new post."
  (
   [title make-subdir?]
   (if (= 0 (count title))
     (do
       (println("Error: Title not specified!"))
       false)
     (gen/make-content-item! {:kind :post
                                    :title title
                                    :slug (gen/title-to-slug title)
                                    :path-extra ""
                                    :make-subdir? make-subdir?
                                    :include-date? true
                                    }))
  )
  (
   []
   (make-post!
     (get-user-input "Post title: " "")
     (prompt-y-or-n "Make directory for post? "))
  ))

(defn make-page!
  "Prompts the user for necessary information and makes a new page."
  (
   [title make-subdir?]
   (if (= 0 (count title))
     (do
       (println("Error: Title not specified!"))
       false)
     (gen/make-content-item! {:kind :page
                                    :slug (gen/title-to-slug title)
                                    :title title
                                    :path-extra ""
                                    :make-subdir? make-subdir?
                                    :include-date? true
                                    }))
  )
  (
   []
   (make-page!
     (get-user-input "Page title: " "")
     (prompt-y-or-n "Make directory for page? "))
  ))

(defn make-project!
  "Prompts the user for necessary information and makes a new project."
  (
   [title]
   (if (= 0 (count title))
     (do
       (println("Error: Title not specified!"))
       false)
     (gen/make-content-item! {:kind :page
                   :slug (gen/title-to-slug title)
                                    :title title
                                    :path-extra "projects"
                                    :make-subdir? true
                                    :include-date? true
                                    }))
  )
  (
   []
   (make-project!
     (get-user-input "Project title: " "") true)
  ))

(defn display-version!
  "Display a version message."
  []
  (println "Cryogen CLI Version 0.2")
  (println "Adapted by Tammy Cravit, tammymakesthings@gmail.com"))

(defn app-banner
  "Display the app startup banner."
  []
  (println "****************************************************************************")
  (println "*                             Cryogen CLI Helper                           *")
  (println "****************************************************************************")
  (display-version!)
  (println ""))

(defn display-help!
  "Display a help/usage message."
  []
  (println "Usage: lein run [command]")
  (println "")
  (println "Available commands:")
  (println "")
  (println "    new-page       Create a new page under pages/")
  (println "    new-post       Create a new blog post under posts/")
  (println "    new-project    Create a new project under pages/projects/")
  (println "    build          Rebuild the site content")
  (println "    tool-help      Display this help message")
  (println "    tool-version   Display a version number")
  (println "")
  (println "If the command is omitted, \"build\" will be run.")
  )

(defn dispatch-arg
  "Dispatch based on the command provided as the first argv arg"
  [command]
  (if (not= command "tool-version") (app-banner))

  (cond
    (= command "new-page") (make-page!)
    (= command "new-post") (make-post!)
    (= command "new-project") (make-project!)
    (= command "build") (gen/build-site!)
    (= command "tool-help") (display-help!)
    (= command "tool-version") (display-version!)
    :else (display-help!)))
