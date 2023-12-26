(defproject ti4-map-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "FIXME"
            :url "FIXME"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.github.discljord/discljord "1.3.1"]
                 [ring/ring-core "1.10.0"]
                 [http-kit "2.3.0"]
                 [compojure "1.7.0"]
                 [TI4_map_generator_discord_bot/TI4_map_generator_discord_bot "1.0-SNAPSHOT-jar-with-dependencies"]
                 [com.fasterxml.jackson.core/jackson-databind "2.15.2"] [com.fasterxml.jackson.core/jackson-core "2.15.2"] ]
  :repl-options {:init-ns ti4-map-bot.core}
  :plugins [[lein-localrepo "0.5.4"]]
  :main ti4-map-bot.core)
