(ns spatial-cascalog.utils
  "Handy utilities"
  (:use [cljts.geom :exclude [empty?]]
        [cljts.io :only (read-wkt-str)]
        [cascalog.api]))

(defn latlon->idx
  [lat lon]
  [(int lat) (int lon)])

(defn split-line [line re n]
  (clojure.string/split line re n))

(defn double-up
  [lat lon]
  (map #(Double/parseDouble %) [lat lon]))


;; JTS-related functions

(defn wkt->jts
  [wkt]
  (read-wkt-str wkt))

(defn latlon->point
  "Make JTS point object from latlon coordinate pair"
  [lat lon]
  (point (c lon lat)))

(defn load-poly
  [path]
  (let [line (slurp path)
        [iso poly-wkt] (split-line line #"," 2)]
    (read-wkt-str (subs poly-wkt 11))))

(defn add-fields
  [& vals]
  vals)
