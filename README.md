# Minimization for `philwalk/uni` community-build3 failure

Original failure: VirtusLab community-build3 weekly run 2026-05-01,
nightly `3.9.0-RC1-bin-20260501-0c8c581-NIGHTLY`, project
`philwalk/uni` — `Test / compileIncremental` failed with `[E007]
Type Mismatch Error` at `src/test/scala/uni/data/DefaultDoubleTests.scala:25:24`.

CB job: <https://github.com/VirtusLab/community-build3/actions/runs/16203614200>

## Repro

This is a vanilla sbt project. `sbt compile` against `3.9.0-RC1-bin-SNAPSHOT`
(or any 3.x except 3.7.0) reproduces the error.

`Lib.scala`:

```scala
object Lib:
  opaque type Mat = String
  opaque type CVec <: Mat = Mat

  extension (m: Mat)
    def tdata: Int = ???
    inline def apply(): Int = m.tdata

  def linspace: CVec = ???
```

`Main.scala`:

```scala
@main def run(): Unit =
  val m = Lib.linspace
  val x: Int = m()
```

## Output

```
-- [E007] Type Mismatch Error: Main.scala:3:15 ----------------
3 |  val x: Int = m()
  |               ^
  |               Found:    (m$proxy1 : (m : Lib.CVec) & $proxy1.CVec)
  |               Required: Lib$_this.Mat
```

## Bisection

| Scala version | Result |
| --- | --- |
| 3.3.6 | fails |
| 3.5.2 | fails |
| 3.6.4 | fails |
| **3.7.0** (uni's pinned version) | **passes** |
| 3.7.1 | fails |
| 3.7.2 | fails |
| 3.7.3 | fails |
| 3.7.4 | fails |
| 3.8.1 | fails |
| 3.9.0-RC1-bin-SNAPSHOT | fails |

3.7.0 is the unique version where the minimized repro compiles. The CB
regression on uni is therefore not a 3.9-specific regression but a
long-standing bug accidentally masked in 3.7.0. Because uni pins
`scala3 = "3.7.0"`, its inline call sites happened to type-check —
once CB bumps the version to 3.9-nightly, the latent bug resurfaces.

## Adjacent issues

* [`scala/scala3#17243`](https://github.com/scala/scala3/issues/17243) —
  same `xxx$proxy1` intersection shape, but with a layered alias
  (`opaque type B = Opaque.A` across two objects) rather than an
  upper-bound (`opaque type CVec <: Mat`).
* [`scala/scala3#17287`](https://github.com/scala/scala3/issues/17287) —
  same general direction (inlining error on a type with an opaque
  upper bound), but uses an abstract type (`type X <: Opaque`) inside
  a trait rather than a second `opaque type` in the same object.
* [`scala/scala3#23137`](https://github.com/scala/scala3/issues/23137)
  (closed) — explicitly noted "works in 3.6.4" but broke in 3.7.0,
  i.e. the opposite shape of regression. Fix landed in 3.7.x; same
  3.7.0 → 3.7.1 boundary as the version pivot above.
