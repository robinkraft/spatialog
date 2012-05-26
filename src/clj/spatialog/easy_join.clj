(ns spatialog.easy-join
  "Namespace demonstrating a spatial join of fire detections with country boundaries."
  (:use [cljts.geom :exclude [empty?]]
        [cljts.relation :exclude [contains?]]
        [cljts.io]
        [cascalog.api]
        [spatialog.utils])
  (:require [cascalog.ops :as c-ops]
            [cascalog.io :as c-io]))

(deffilterop [intersects-op [poly]] [lat lon]
  "Parameterized intersect checks envelope intersect before checking full polygon"
  (let [pt (latlon->point lat lon)]
    (if (intersects? (envelope poly) pt)
      (intersects? poly pt))))

(deffilterop [intersects-env-op [poly]] [lat lon]

  "Parameterized intersect only checks envelope intersect"
  (let [pt (latlon->point lat lon)]
    (intersects? (envelope poly) pt)))

(defn intersector [poly lat lon]
  "Standard intersection"
  (intersects? poly (latlon->point lat lon)))

(defn poly-tap
  "Tap reads polygons from csv of WKT strings, returns iso code and
   JTS polygon geometry"
  [polys-path]
  (let [src (hfs-textline polys-path)]
    (<- [?iso ?poly-geom]
        (src ?line)
        (split-line ?line #"," 2 :> ?iso ?poly-str)
        (subs ?poly-str 11 :> ?wkt)
        (wkt->jts ?wkt :> ?poly-geom))))

(defn points-tap
  "Loads fires from csv file"
  [path]
  (let [pts-src (hfs-textline path)]
    (<- [?lat ?lon]
        (pts-src ?line)
        (split-line ?line #"," 3 :> ?lat-str ?lon-str _)
        (double-up ?lat-str ?lon-str :> ?lat ?lon))))

(defn parameterized-join
  [poly-path points-path]
  (let [[iso poly-geom] (first (??- (poly-tap poly-path)))
        pts-tap (points-tap points-path) ]
    (<- [?count ?iso]
        (pts-tap ?lat ?lon)
        (add-fields iso :> ?iso)
        (intersects-env-op [poly-geom] ?lat ?lon)
        (c-ops/count ?count))))
