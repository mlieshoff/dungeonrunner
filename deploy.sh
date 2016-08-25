#!/bin/sh
mvn clean install
# ant

cp bukkit/target/dungeonrunner-bukkit-1.0.0.jar ~/Spiele/dr_bukkit/plugins
rm ~/Spiele/dr_bukkit/plugins/dungeonrunner/dungeonrunner.db

cp bukkit/target/dungeonrunner-bukkit-1.0.0.jar ~/Spiele/bk/plugins
rm ~/Spiele/bk/plugins/dungeonrunner/dungeonrunner.db