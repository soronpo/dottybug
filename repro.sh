#!/bin/bash
# Returns 0 if cyclic error reproduced on incremental.
FILE="src/main/scala/MutableDB.scala"
echo "[repro] clean compile..."
CLEAN_OUT=$(sbtn.bat 'clean; compile' 2>&1)
if ! echo "$CLEAN_OUT" | grep -q "success"; then
  echo "[repro] CLEAN COMPILE FAILED — cannot test"
  echo "$CLEAN_OUT" | tail -20
  exit 2
fi
TMP=$(mktemp)
head -1 "$FILE" > "$TMP"
echo "// repro-trigger" >> "$TMP"
tail -n +2 "$FILE" >> "$TMP"
mv "$TMP" "$FILE"
echo "[repro] incremental compile..."
INC_OUT=$(sbtn.bat compile 2>&1)
grep -v "^// repro-trigger" "$FILE" > "$FILE.tmp" && mv "$FILE.tmp" "$FILE"
if echo "$INC_OUT" | grep -q "Cyclic reference involving val <import>" && \
   echo "$INC_OUT" | grep -q "stubs.scala"; then
  echo "[repro] OK — reproduced cyclic error on incremental"
  exit 0
else
  echo "[repro] FAIL — bug did not reproduce"
  echo "$INC_OUT" | tail -20
  exit 1
fi
