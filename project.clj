(defproject tammymakesthings "0.1.0"
  :description "Tammy Makes Things web site (from cryogen)"
  :url "https://github.com/tammymakesthings/tammymakesthings"
  :license
  {
   :name "Eclipse Public License"
   :url "http://www.eclipse.org/legal/epl-v10.html"
  }
  :dependencies
  [
   [org.clojure/clojure "1.10.0"]
   [ring/ring-devel "1.7.1"]
   [compojure "1.6.1"]
   [ring-server "0.5.0"]
   [cryogen-flexmark "0.1.2"]
   [org.clojure/tools.cli "1.0.194"]
   [cryogen-core "0.3.1"]
   [philoskim/debux "0.6.5"]
   [slugger "1.0.1"]
  ]
  :plugins
  [
   [lein-ring "0.12.5"]
  ]
  :aliases
  {
   { "new-page"     ["run" "-m" "tammymakesthings.core/main" "new-page"   ] }
   { "new-post"     ["run" "-m" "tammymakesthings.core/main" "new-post"   ] }
   { "new-project"  ["run" "-m" "tammymakesthings.core/main" "new-project"] }
   { "build"        ["run" "-m" "tammymakesthings.core/main" "build"      ] }
   { "tool-help"    ["run" "-m" "tammymakesthings.core/main" "help"       ] }
   { "tool-version" ["run" "-m" "tammymakesthings.core/main" "version"    ] }
  }
  :main tammymakesthings.core
  :ring
  {
   :init tammymakesthings.server/init
   :handler tammymakesthings.server/handler
  }

  :jar-name "tammymakesthings-site.jar"
  :uberjar-name "tammymakesthings-site-standalone.jar"
  :filespecs
  [
    {:type :paths :paths ["content", "src", "themes"]}
    {:type :path  :path  "project.clj"}
  ]
)
