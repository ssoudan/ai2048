(ns ai2048.core  
  (:require [ai2048.play :as play]
            [ai2048.board :as board])
  (:gen-class))

(defn -main
  "I do a lot now!"
  [& args]
  ;; work around dangerous default behaviour in Clojure - Don't know what it mean but that scary. Will keed it!
  (alter-var-root #'*read-eval* (constantly false))
  (println "Get ready!")
  (println "GO!")
  (time (
         try (
              nth (iterate play/play-smartly (board/new-board)) 4000) 
         (
          catch Exception e (
                             println (.getMessage e))))))
