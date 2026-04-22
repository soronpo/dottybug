## Scala3 minimized bug — incremental cyclic reference involving val \<import\>

### Symptom

**Windows-only** — tested and does not reproduce on WSL (Ubuntu).

Clean compile (`sbtn "clean; compile"`) succeeds. Touching `src\main\scala\MutableDB.scala` (any change that triggers an incremental recompile) then makes the next `sbtn compile` fail with:

```
[error] -- [E046] Cyclic Error: src/main/scala/stubs.scala:31:26
[error] 31 |    given c1: ExactOp2Aux[CarryOp, DFC, DFValTP[DFType]] = ???
[error]    |                          ^
[error]    |                          Cyclic reference involving val <import>
```

The `val <import>` referenced in the error is `import DFVal.Ops.CarryOp` at the top of `stubs.scala`.

### Reproduction

Run in **Windows cmd** from the project root:

```bat
sbtn "clean; compile"
echo.>> src\main\scala\MutableDB.scala
sbtn compile
```

The first command succeeds; the third fails with the cyclic error. `sbtn` (the thin client) is required so the persistent sbt server state is preserved between invocations — plain `sbt` starts a fresh JVM each time and the bug does **not** reproduce.

Or just run `repro.bat`, which automates the above and exits 0 on successful reproduction.

### Notes

- Reproduces on Scala `3.8.3` (nightly resolver enabled in `build.sbt`).
- The cycle is between `stubs.scala` (which `import`s `DFVal.Ops.CarryOp`) and `DFVal.scala` (which `export`s givens defined in `stubs.scala`'s `DFXInt.Ops`).
- The `MutableDB.scala` file is only used as the trigger for incremental recompilation — its content is not part of the cycle.
