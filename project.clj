(defproject odo "0.1.0"
  :description "Shape shifter"
  :license "Eclipse Public License 1.0"
  :url "https://odo-clj.herokuapp.com"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [com.cemerick/pomegranate "0.3.0"]
                 [ring/ring-jetty-adapter "1.2.0"]]
  :ring {:handler odo.handler/app}
  :main ^:skip-aot odo.handler
  :uberjar-name "odo-standalone.jar"
  :min-lein-version "2.4.3")
