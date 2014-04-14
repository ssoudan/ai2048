(defproject ai2048 "0.1.0-SNAPSHOT"
  :description "ai2048: minimax-based solver for 2048 game"
  :url "http://github.com/ssoudan/ai2048"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"] [org.clojure/core.match "0.2.1"]]
  :main ai2048.core
  :plugins [[lein-cloverage "1.0.2"]]
  )
