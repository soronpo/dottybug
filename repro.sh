#!/usr/bin/env bash
# Reproduces the post-#25900 incremental cyclic-export bug on Scala 3.9.0-RC1.
#
# Pure scalac, two-stage separate compilation (no sbt/Zinc/OS specifics):
#   stage 1 - clean compile of all sources              -> out1   (succeeds)
#   stage 2 - recompile stubs.scala + hdl.scala against -> out2   (cyclic error)
#             the stage-1 TASTy in out1
#
# Exits 0 iff the cyclic error is reproduced, 1 if not, 2 on setup failure.
set -uo pipefail
cd "$(dirname "$0")"

SRC=src/main/scala
OUT1=out1
OUT2=out2
CPFILE=compiler.cp

# Resolve the 3.9.0-RC1 compiler classpath once (cached in compiler.cp).
if [ ! -s "$CPFILE" ]; then
  echo "[repro] resolving Scala 3.9.0-RC1 compiler classpath via sbt..."
  if command -v sbtn >/dev/null 2>&1; then SBT=sbtn; else SBT=sbt; fi
  "$SBT" printClasspath >/dev/null 2>&1 || { echo "[repro] failed to resolve compiler classpath"; exit 2; }
fi
CP="$(cat "$CPFILE")"

run_scalac() { java -cp "$CP" dotty.tools.dotc.Main -usejavacp "$@"; }

rm -rf "$OUT1" "$OUT2"
mkdir -p "$OUT1" "$OUT2"

echo "[repro] stage 1: clean compile of all sources -> $OUT1"
if ! run_scalac -d "$OUT1" "$SRC"/*.scala; then
  echo "[repro] STAGE 1 FAILED -- cannot test"
  exit 2
fi

echo "[repro] stage 2: recompile stubs.scala + hdl.scala against $OUT1 (expecting cyclic error)..."
INC_OUT="$(run_scalac -classpath "$OUT1" -d "$OUT2" "$SRC/stubs.scala" "$SRC/hdl.scala" 2>&1)"
echo "$INC_OUT"

if echo "$INC_OUT" | grep -q "Cyclic reference involving val <import>"; then
  echo "[repro] OK -- reproduced cyclic error on partial recompile (bug survives #25900)"
  exit 0
else
  echo "[repro] FAIL -- bug did not reproduce"
  exit 1
fi
