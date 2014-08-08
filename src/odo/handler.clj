(ns odo.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.pomegranate :refer [add-dependencies]]
            [ring.adapter.jetty :as jetty]
            [clojure.java.io :as io]))

(def redirect-to-slash {:status 302 :content "redirecting"
                        :headers {:content-type "text/plain" :location "/"}})

(def pending {:status 200 :content "pending, please wait"
              :headers {:content-type "text/plain"}})

(def app (atom {}))

(defn become [artifact handler]
  (add-dependencies :coordinates [(read-string artifact)] ; hahahaha
                    :repositories {"clojars" "https://clojars.org/repo"})
  (let [s (symbol handler)]
    (require (symbol (namespace s)))
    (reset! app (deref (resolve s)))))

(defroutes odo-routes
  (GET "/" [] (slurp (io/resource "public/index.html")))
  (POST "/" [dep handler]
        (reset! app pending)
        (future (become dep handler))
        redirect-to-slash)
  (route/not-found "Not Found"))

(def odo (handler/site odo-routes))

(defn wrap-reset [h]
  (fn [req]
    (if (= "/_odo-reset" (:uri req))
      (do (reset! app odo)
          redirect-to-slash)
      (h req))))

(defn -main [& [port]]
  (reset! app odo)
  (let [port (Integer. (or port 5000))]
    (jetty/run-jetty (wrap-reset (fn [req] (@app req)))
                     {:port port :join? false})))
