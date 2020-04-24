;;;; -*- mode: clojure ; fill-column: 78 -*-
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; tammymakesthings - Static blog generator for tammymakesthings.com
;;;; File         : core.clj
;;;; Description  : Core functions and entrypoint for the site generator.
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

(ns tammymakesthings.core
  (:require [cryogen-core.compiler :refer [compile-assets-timed]]
            [clojure.java.io :as io]
            [tammymakesthings.helpers :as helpers]
            [tammymakesthings.generator :as gen]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [cryogen-core.plugins :refer [load-plugins]])
  (:gen-class))

(use 'debux.core)
(use 'slugger.core)

(defn dispatch-arg
  "Dispatch based on the command provided as the first argv arg"
  [command]
  (cond
    (= command "new-page") (gen/make-page!)
    (= command "new-post") (gen/make-post!)
    (= command "new-project") (gen/make-project!)
    (= command "build") (gen/build-site!)
    (= command "help") (helpers/display-help!)
    (= command "version") (helpers/display-version!)
    :else (helpers/display-help!)))

(defn -main
  [& args]
  (load-plugins)
  (if (empty? args)
    (gen/build-site!)
    (dispatch-arg (first args)))
  (System/exit 0))
