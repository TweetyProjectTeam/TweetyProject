#!/bin/sh
# Runs the evaluation driver; build first with: mvn -f org-tweetyproject-arg-adf/bench/pom.xml package
# Usage: driver.sh generate|run|report [options]   (see Driver.java)
BENCH="$(cd "$(dirname "$0")" && pwd)"
exec java -Dbench.home="$BENCH" -cp "$BENCH/driver/target/classes:$(cat "$BENCH/driver/target/classpath.txt")" \
    org.tweetyproject.arg.adf.bench.driver.Driver "$@"
