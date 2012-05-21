(defproject cascalog-spatial "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :repositories {"conjars" "http://conjars.org/repo/"}
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :jvm-opts ["-XX:MaxPermSize=128M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M" "-Xmx1048M" "-server"]
  :dependencies [[cljts "0.1.0"]
                 [cascalog "1.9.0-wip8"]]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
                     [midje-cascalog "0.4.0"]])