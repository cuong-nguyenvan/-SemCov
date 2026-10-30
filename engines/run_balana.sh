#!/bin/sh
# usage: engines/run_balana.sh <list.tsv> <out.tsv>   (line: id \t policy.xml \t request.xml)
R=$(cd "$(dirname "$0")/.." && pwd)
exec java --add-exports java.xml/com.sun.org.apache.xerces.internal.util=ALL-UNNAMED \
  --add-exports java.xml/com.sun.org.apache.xerces.internal.impl=ALL-UNNAMED -Duser.timezone=UTC \
  -cp "$R/build/balana/classes:$R/build/balana/harness" BalanaRunner "$1" "$2"
