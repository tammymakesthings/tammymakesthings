;;;; -*- mode: clojure ; fill-column: 78 -*-
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; tammymakesthings - Static blog generator for tammymakesthings.com
;;;; File         : helpers.clj
;;;; Description  : Generic helper functions.
;;;; Last Updated : Time-stamp: <2020-04-24 17:45:42 tammy>
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

(ns tammymakesthings.helpers
  (:require [cryogen-core.compiler :refer [compile-assets-timed]]
            [clojure.java.io :as io]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [cryogen-core.plugins :refer [load-plugins]])
  (:gen-class))

(def app-version-string "1.0")

(use 'debux.core)
(use 'slugger.core)

(defn get-user-input
  "Prompts the user to enter a string."
  [prompt default]

  ([] (get-user-input "Your input" nil))
  ([default]
    (let [input (clojure.string/trim (read-line))]
      (if (empty? input)
        default
        (clojure.string/lower-case input)))))

(defn prompt-y-or-n
  "Prompt the user for a yes or no input."
  [prompt]
  (let [input (get-user-input prompt "no")]
    (clojure.string/starts-with? "y")))

(defn display-version!
  "Display a version message."
  []
  (println (str("Cryogen CLI Helpers version " app-version-string)))
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
  (println "    new-page       Create a new page under pages/")
  (println "    new-post       Create a new blog post under posts/")
  (println "    new-project    Create a new project under pages/projects/")
  (println "    build          Rebuild the site content")
  (println "    help           Display this help message")
  (println "    version        Display a version number")
  (println "")
  (println "If the command is omitted, \"build\" will be run.")
  )

