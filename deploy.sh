#!/bin/sh
mvn clean install
# ant
cp bukkit/target/dungeonrunner-bukkit-1.0.0.jar ~/Spiele/bk/plugins
rm ~/Spiele/bk/plugins/dungeonrunner/dungeonrunner.db