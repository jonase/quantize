(ns quantize.core
  (:require [datomic.api :as d]
            [datomic.codeq.core :as codeq]
            [clojure.pprint :as pp]
            [quantize.queries :refer [queries]]))

(defn db-uri [project]
  (let [storage (or (-> project :quantize :storage) "mem")
        host (or (-> project :quantize :host) "localhost")
        port (or (-> project :quantize :port) 4334)
        name (or (-> project :quantize :name) (:name project))]
    (if (= storage "mem")
      (format "datomic:mem://%s" name)
      (format "datomic:%s://%s:%s/%s" storage host port name))))

(defn run [project & [query & args]]
  (let [uri (db-uri project)]
    (codeq/main uri)
    (when query
      (let [query (read-string query)
            args (map read-string args)
            res (apply (queries query)
                       (-> uri d/connect d/db)
                       args)]
        (pp/pprint res)
        (flush)))
    (shutdown-agents)
    (System/exit 0)))


