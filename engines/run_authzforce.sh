#!/bin/sh
# usage: engines/run_authzforce.sh <list.tsv> <out.tsv>   (line: id \t policy.xml \t request.xml)
R=$(cd "$(dirname "$0")/.." && pwd)
exec java -Duser.timezone=UTC -cp "$R/build/authzforce/classes:$R/build/authzforce/lib/*" AuthzForceRunner "$1" "$2"
