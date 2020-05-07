(defproject tammymakesthings "0.1.0"
  :description "Tammy Makes Things web site (from cryogen)"
  :url "https://github.com/tammymakesthings/tammymakesthings"
  :main tammymakesthings.core

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
    [philoskim/debux "0.6.5"]
    [slugger "1.0.1"]
    [enlive "1.1.6"]
    [camel-snake-kebab "0.4.1"]
    [cheshire "5.9.0"]
    [clj-rss "0.2.5"]
    [clj-text-decoration "0.0.3"]
    [hawk "0.2.11"]
    [hiccup "1.0.5"]
    [io.aviso/pretty "0.1.37"]
    [me.raynes/fs "1.4.6"]
    [pandect "0.6.1"]
    [prismatic/schema "1.1.12"]
    [selmer "1.12.18"]
  ]

  :profiles
  {
    :dev
    {
      :dependencies
      [
        [speclj "3.3.0"]
        [kerodon "0.9.1"]
      ]
    }
  }

  :plugins
  [
    [lein-ring "0.12.5"]
    [speclj "3.3.0"]
  ]

  :test-paths
  [
    "spec"
  ]

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
