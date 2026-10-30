#!/bin/sh
# usage: engines/run_openaz.sh <list.tsv> <out.tsv>   (line: id \t policy.xml \t request.xml)
R=$(cd "$(dirname "$0")/.." && pwd)
D=${XPDP_DEPS:-$R/deps}
CP="$R/build/openaz/classes:$R/build/openaz/harness:$(ls $D/openaz-lib/*.jar | tr '\n' ':')"
exec java -Duser.timezone=UTC -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.NoOpLog \
  -cp "$CP" OpenAZRunner "$1" "$2"
