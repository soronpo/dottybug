## Scala3 minimized bug — incremental cyclic reference involving val \<import\>

### Symptom

**Windows-only** — tested and does not reproduce on WSL (Ubuntu).

Clean compile (`sbt "clean; compile"`) succeeds. Touching `src/main/scala/MutableDB.scala` (any change that triggers an incremental recompile) then makes the next `sbt compile` fail with:

```
[error] -- [E046] Cyclic Error: src/main/scala/stubs.scala:31:26
[error] 31 |    given c1: ExactOp2Aux[CarryOp, DFC, DFValTP[DFType]] = ???
[error]    |                          ^
[error]    |                          Cyclic reference involving val <import>
```

The `val <import>` referenced in the error is `import DFVal.Ops.CarryOp` at the top of `stubs.scala`.

### Reproduction

```bash
sbt "clean; compile"       # succeeds
echo "// touch" >> src/main/scala/MutableDB.scala
sbt compile                # fails with the cyclic error
```

Or run `bash repro.sh` which automates this and exits 0 on successful reproduction.

### Notes

- Reproduces on Scala `3.8.3` (nightly resolver enabled in `build.sbt`).
- The cycle is between `stubs.scala` (which `import`s `DFVal.Ops.CarryOp`) and `DFVal.scala` (which `export`s givens defined in `stubs.scala`'s `DFXInt.Ops`).
- The `MutableDB.scala` file is only used as the trigger for incremental recompilation — its content is not part of the cycle.
