(ns app.api
  (:refer-clojure :exclude [get])
  (:require
   [clojure.string :as str]
   [ajax.core :as ajax]))

(def api-url "http://localhost:3000/api")

(defn endpoint
  "Concat any params to api-url separated by /"
  [& params]
  (str/join "/" (concat [api-url] params)))

(defn auth-header
  "Get user token and format for API authorization"
  [db]
  (when-let [token (get-in db [:user :token])]
    {"Authorization" (str "Token " token)}))

;; opts {:db db :params params :headers headers}
(defn http-request
  ([method uri on-success on-failure]
   {:method                 method
    :uri                    uri
    :format                 (ajax/json-request-format)
    :response-format        (ajax/json-response-format {:keywords? true})
    :on-success             on-success
    :on-failure             on-failure})
  ([method uri on-success on-failure opts]
   (merge
    opts
    {:method                 method
     :uri                    uri
     :format                 (ajax/json-request-format)
     :response-format        (ajax/json-response-format {:keywords? true})
     :on-success             on-success
     :on-failure             on-failure})))

(def get (partial http-request :get))

(def post (partial http-request :post))
