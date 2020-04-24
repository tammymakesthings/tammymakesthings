(ns cryogen.core
  (:require [cryogen-core.compiler :refer [compile-assets-timed]]
            [clojure.java.io :as io]
            [cryogen-core.plugins :refer [load-plugins]])
  (:gen-class))

(def app-version "1.0")

(defn build-site!
  []
  (println "In build-site")
  (compile-assets-timed))

(defn content-basedir
  "Get the base directory for the content."
  []
  "/home/tammy/blog"
)

(defn path-join
  "Join path components into a string path."
  [p & ps]
  (str (.normalize (java.nio.file.Paths/get p (into-array String ps)))))

(defn path-for
  "Get the path for a content file"
  ([contentdef] (path-for (contentdef :kind)
                          (contentdef :path-extra)
                          (contentdef :slug)
                          (contentdef :include-date?)
                          (contentdef :make-subdir?)))
  ([kind slug] (path-for kind "" slug false false))
  ([kind path-extra slug] (path-for kind path-extra slug false false))
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

(defn title-to-slug
  "Convert a post title to a slug."
  [title]
  (clojure.string/replace (clojure.string/lower-case title) " " "-"))

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

(defn make-page!
  "Create a new page."
  []
  (let [title (get-user-input "Page title:" "")]
    (if (empty? title)
      (println "No title provided - aborting!")
      (create-new-content! {
                              :title         title
                              :slug          (title-to-slug title)
                              :kind          :page
                              :include-date? false
                              :make-subdir?  (prompt-y-or-n "Create directory? ")
                            }))))

(defn make-post!
  "Create a new blog post."
  []
  (let [title (get-user-input "Page title:" "")]
    (if (empty? title)
      (println "No title provided - aborting!")
      (create-new-content! {
                              :title         title
                              :slug          (title-to-slug title)
                              :kind          :post
                              :include-date? true
                              :make-subdir?  false
                            }))))

(defn make-project!
  "Create a new project under pages/projects."
  []
  (let [title (get-user-input "Project title:" "")]
    (if (empty? title)
      (println "No title provided - aborting!")
      (create-new-content! {
                              :title         title
                              :slug          (title-to-slug title)
                              :kind          :page
                              :path_extra    "projects"
                              :include-date? false
                              :make-subdir?  true
                            }))))

(defn display-version!
  "Display a version message."
  []
  (println (str("Cryogen CLI Helpers version " app-version)))
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

(defn dispatch-arg
  "Dispatch based on the command provided as the first argv arg"
  [command]
  (cond
    (= command "new-page") (make-page!)
    (= command "new-post") (make-post!)
    (= command "new-project") (make-project!)
    (= command "build") (build-site!)
    (= command "help") (display-help!)
    (= command "version") (display-version!)
    :else (display-help!)
  ))

(defn -main
  [& args]
  (load-plugins)
  (app-banner)
  (if (empty? args)
      (build-site!)
    (dispatch-arg (first args)))
  (System/exit 0))
