(ns spatial-cascalog.core
  (:use [cljts.geom :exclude [empty?]]
        [cljts.relation :exclude [contains?]]
        [cljts.io :only (read-wkt-str)]
        [spatial-cascalog utils]
        [cascalog.api]
        [meridian/clj-jts "0.0.1"]
        )
  (:require [cascalog.ops :as c-ops]
            [cascalog.io :as io]))