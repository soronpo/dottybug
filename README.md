## Scala 3 minimized bug — incremental cyclic reference involving `val <import>` (survives the #25894 fix)

### Background

This is a follow-up minimization to
[`inc_export_cyclic_err`](https://github.com/soronpo/dottybug/tree/inc_export_cyclic_err),
which reported [scala/scala3#25894](https://github.com/scala/scala3/issues/25894).
That issue was fixed by [scala/scala3#25900](https://github.com/scala/scala3/pull/25900),
shipped in **Scala 3.9.0-RC1**.

The fix breaks the cycle when a **single** file is recompiled against the TASTy
of its siblings. This minimization shows the bug **still reproduces on 3.9.0-RC1**
when **two source units are recompiled together**, one of which re-exports the
offending givens at package level.

### Symptom

A clean compile succeeds. A partial recompile of `stubs.scala` **and**
`hdl.scala` against the previously-compiled output then fails with:

```
-- [E046] Cyclic Error: src/main/scala/stubs.scala:15:26
15 |    given c1: ExactOp2Aux[CarryOp] = ???
   |                          ^
   |                          Cyclic reference involving val <import>
```

The `val <import>` is `import DFVal.Ops.CarryOp` at the top of `stubs.scala`.

### Reproduction

```bash
./repro.sh
```

It performs a pure-scalac two-stage separate compilation (no sbt/Zinc/OS
specifics required) and exits `0` when the cyclic error is reproduced:

```bash
# stage 1 — clean build, all files               (succeeds)
scalac -d out1 src/main/scala/*.scala
# stage 2 — recompile two units against stage 1  (cyclic error)
scalac -classpath out1 -d out2 src/main/scala/stubs.scala src/main/scala/hdl.scala
```

`repro.sh` resolves the `3.9.0-RC1` compiler classpath via `sbt printClasspath`
(cached in `compiler.cp`) and drives `scalac` directly.

### The cycle

- `stubs.scala` opens with `import DFVal.Ops.CarryOp` and defines the given
  `DFXInt.Ops.c1` whose type references `CarryOp`.
- `DFVal.scala` (loaded from TASTy in stage 2) has `export DFXInt.Ops.c1`.
- Resolving the import forces `DFVal` from TASTy → its `export` chains into the
  `c1` given being defined in the file currently being typed → cycle.

### Why it survives #25900

#25900 force-completes the sibling `<src>$package` classes that come from the
classpath (`isDefinedInBinary`) before any import completer runs, pre-resolving
their exports. That is enough when only one file is recompiled.

Here a **second** source unit, `hdl.scala`, is recompiled at the same time and
re-exports the same givens via a top-level `export hdl.*`. Because `hdl` is now
a *source* sibling (not `isDefinedInBinary`), it is not force-completed by the
fix, and the export chain through it is resolved lazily during the import's
completer — re-introducing the cycle.

Controls (verified):

- Recompiling **all** sources against the stale stage-1 output also fails.
- **Merging** `hdl.scala` into `stubs.scala` (one source unit) **succeeds** —
  that is exactly the single-unit case #25900 fixed.

### Notes

- Reproduces on Scala `3.9.0-RC1`.
- Pure scalac; no compiler plugin, no sbt incremental, not OS-specific.
- The file order matters: `stubs.scala` must be typed before `hdl.scala`
  (it is listed first in stage 2), mirroring the order in which the package
  units are processed in the original incremental build.
