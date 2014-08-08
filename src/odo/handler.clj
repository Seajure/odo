(ns odo.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.pomegranate :refer [add-dependencies]]
            [ring.adapter.jetty :as jetty]
            [clojure.java.io :as io]))

(declare app)

(defn become [artifact handler]
  (prn :becoming artifact handler)
  (add-dependencies :coordinates [(read-string artifact)] ; hahahaha
                    :repositories {"clojars" "http://clojars.org/repo"})
  (let [s (symbol handler)
        sn (symbol (namespace s))]
    (prn :requiring sn)
    (require sn)
    (alter-var-root #'app (constantly (deref (resolve s))))
    (prn :altered)))

(defroutes app-routes
  (GET "/" [] (slurp (io/resource "public/index.html")))
  (POST "/" [dep handler] (become dep handler))
  (route/resources "public/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn -main [& [port]]
  (let [port (Integer. (or port 5000))]
    (jetty/run-jetty #'app {:port port :join? false})))
