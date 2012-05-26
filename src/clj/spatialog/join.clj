(ns spatialog.join
  "Namespace demonstrating a spatial join of fire detections with country boundaries."
  (:use [cljts.geom :exclude [empty?]]
        [cljts.relation :exclude [contains?]]
        [cljts.io]
        [spatialog.utils]
        [cascalog.api])
  (:require [cascalog.ops :as c-ops]
            [cascalog.io :as c-io]))

(defn fires-latlon-tap
  [path]
  (let [src (hfs-textline path)]
    (<- [?lat-idx ?lon-idx ?lat ?lon]
        (src ?line)
        (split-line ?line #"," 3 :> ?lat-str ?lon-str _)
        (double-up ?lat-str ?lon-str :> ?lat ?lon)
        (int ?lat :> ?lat-idx)
        (int ?lon :> ?lon-idx))))

(defn get-latlon-idxs
  [points-path]
  (let [pts-src (fires-latlon-tap points-path)]
    (<- [?lat-idx ?lon-idx]
        (pts-src ?lat-idx ?lon-idx _ _))))

(defmapcatop val->latlon-idx [val]
  (let [lat-max 180
        lon-max 360]
    (for [lat-idx (range -89 90);;(range -21 -19)
          lon-idx (range -179 180)] ;;(range 138 142)
      [lat-idx lon-idx])))

(defn country-tap-wkt-idxs
  "Tap reads polygons from csv of WKT strings, returns iso code,
   JTS polygon geometry and JTS polygon envelope geometry"
  [pts-path polys-path]
  (let [src (hfs-textline polys-path)
        idxs-src (get-latlon-idxs pts-path)]
    (<- [?lat-idx ?lon-idx ?iso ?wkt]
        (src ?line)
        (split-line ?line #"," 2 :> ?iso ?poly-str)
        (subs ?poly-str 11 :> ?wkt)
        (idxs-src ?lat-idx ?lon-idx)
        (cross-join))))

(defn latlon-poly-intersect-wkt
  "1:1 point-to-polygon intersection returns `attr` on intersect"
  [lat lon poly-wkt attr]
  (let [pt (mk-point lat lon)
        poly (wkt->jts poly-wkt)]
    (if (intersects? pt (envelope poly))
      (if (intersects? pt poly)
        (name attr)))))

(defn wrap-intersect-wkt
  [lat lon lat-idx lon-idx poly-wkt attr]
  (latlon-poly-intersect-wkt lat lon poly-wkt attr))

(defn run-query
  [points-path polys-path]
  (let [polys-src (country-tap-wkt-idxs points-path polys-path)
        pts-src (fires-latlon-tap points-path)]
    (<- [?iso ?count]
        (pts-src ?lat-idx ?lon-idx ?lat ?lon)
        (polys-src ?lat-idx ?lon-idx ?iso ?poly-wkt)
        (wrap-intersect-wkt ?lat ?lon ?lat-idx ?lon-idx ?poly-wkt ?iso)
        (c-ops/count ?count))))