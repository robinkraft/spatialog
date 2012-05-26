(ns spatialog.core
  (:use [cljts.geom :exclude [empty?]]
        [cljts.relation :exclude [contains?]]
        [cljts.io :only (read-wkt-str)]
        [spatialog.utils]
        [cascalog.api])
  (:require [cascalog.ops :as c-ops]
            [cascalog.io :as io]))