(ns quantize.queries
  (:require [datomic.api :refer [q]]))

;; From http://blog.datomic.com/2012/10/codeq.html
(def rules
  '[[(node-files ?n ?f)
     [?n :node/object ?f]
     [?f :git/type :blob]]
    [(node-files ?n ?f)
     [?n :node/object ?t]
     [?t :git/type :tree] 
     [?t :tree/nodes ?n2]
     (node-files ?n2 ?f)]
    [(object-nodes ?o ?n)
     [?n :node/object ?o]]
    [(object-nodes ?o ?n)
     [?n2 :node/object ?o]
     [?t :tree/nodes ?n2]
     (object-nodes ?t ?n)]
    [(commit-files ?c ?f)
     [?c :commit/tree ?root]
     (node-files ?root ?f)]
    [(commit-codeqs ?c ?cq)
     (commit-files ?c ?f)
     [?cq :codeq/file ?f]]
    [(file-commits ?f ?c)
     (object-nodes ?f ?n)
     [?c :commit/tree ?n]]
    [(codeq-commits ?cq ?c)
     [?cq :codeq/file ?f]
     (file-commits ?f ?c)]])

(defn first-defined [db name]
  (let [;; From http://blog.datomic.com/2012/10/codeq.html
        query '[:find ?src (min ?date)
                :in $ % ?name 
                :where
                [?n :code/name ?name]
                [?cq :clj/def ?n]
                [?cq :codeq/code ?cs]
                [?cs :code/text ?src]
                [?cq :codeq/file ?f]
                (file-commits ?f ?c)
                (?c :commit/authoredAt ?date)]
        res (q query db rules name)]
    (-> res first second)))

(defn initial-commit [db]
  (ffirst (q '[:find (min ?date)
               :where
               [_ :commit/committedAt ?date]]
             db)))
          
(defn commit-count [db]
  (ffirst (q '[:find (count ?c)
               :where
               [?c :git/type :commit]]
             db)))

(defn authors [db]
  (->> (q '[:find ?email (count ?commit)
            :where
            [?commit :commit/author ?author]
            [?author :email/address ?email]]
          db)
       (sort-by second)
       reverse))

(defn query [db query & args]
  (apply q query db args))

(def queries
  {:initial-commit initial-commit
   :commit-count commit-count
   :first-defined first-defined
   :authors authors
   :q query})

