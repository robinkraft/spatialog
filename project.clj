(defproject net.robinkraft/spatialog "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :repositories {"conjars" "http://conjars.org/repo/"}
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :jvm-opts ["-XX:MaxPermSize=128M"
             "-XX:+UseConcMarkSweepGC"
             "-Xms1024M" "-Xmx1048M" "-server"]
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [cljts "0.1.0"]
                 [cascalog "1.9.0-wip8"]]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
                     [midje-cascalog "0.4.0"]])
