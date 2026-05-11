# Minimized repro: lowmelvin/hammer-scala (scala/scala3#26018)

Two-file minimization of the
[`scala/scala3#26018`](https://github.com/scala/scala3/issues/26018)
regression originally surfaced by `lowmelvin/hammer-scala` in the Scala
3 community-build3 nightly.

The previous single-file repro pushed onto this branch failed on **every**
3.x release including the last-known-good `3.6.4` — it didn't isolate
the regression. This version was bisected against the actual project
tests; it compiles cleanly under `3.6.4` and fails under `3.7.4` (and
later, including the latest nightly), with the same `[E172] auto.given_X
does not match` shape that `community-build3` reports.

## Reproduce

```
sbt 'test/compile'
```

Pinned to `3.7.4` because that's the earliest stable that exhibits the
bug — no nightly toolchain required.

The build is split into two subprojects on purpose: the bug only
manifests when `Test.scala` is compiled against `Lib.scala`'s **TASTy**,
not when both files are compiled in the same invocation. Putting all
sources under a single project / single source dir hides the regression.

## Bisection (vs `3.6.4`)

| Compiler           | `lib` compile | `test` compile |
|--------------------|---------------|----------------|
| `3.6.4`            | OK (warnings) | OK             |
| `3.7.0` … `3.7.4`  | OK (warnings) | E172           |
| `3.8.x`            | OK (warnings) | E172           |
| `3.9.0-NIGHTLY`    | OK (warnings) | E172           |

Symptom on 3.7.4+:

```
-- [E172] Type Error: test/src/main/scala/Test.scala:14:30 --
   No given instance of type mh.Hammer[usage.A, usage.B] was found.
   I found:
       mh.derived[usage.A, usage.B](
         usage.A.$asInstanceOf[ Mirror.Product{… "A" …} ],
         usage.B.$asInstanceOf[ Mirror.Product{… "B" …} ]
       )
   But given instance derived in package mh does not match
   type mh.Hammer[usage.A, usage.B].
```

The diagnostic is internally inconsistent: `derived[A, B]` instantiated
with `Mirror.ProductOf[A]` and `Mirror.ProductOf[B]` *does* have type
`Hammer[A, B]`, yet the typer rejects it. The mismatch arises during
inline expansion of `summonInline[Hammer[t, O]]` inside the recursive
auto-derivation chain.

## Trigger ingredients

Each of the following is necessary to reproduce — removing or rewriting
any one of them makes the bug disappear:

1. The recursive `summonInline[Hammer[t, O]]` inside an `inline def`
   driven by an `inline match erasedValue[Ts]` over a tuple type.
2. The `lazy val (h, idx) = summonFirst[…]` **destructured `lazy val`**
   inside the inline-emitted anonymous class body. Replacing it with
   `val (h, idx)`, `lazy val tup` + `lazy val h = tup._1`, or
   collapsing the tuple to a single value, all suppress the bug.
3. `h.hammer(source.asInstanceOf)` — the **untyped `asInstanceOf`** on
   the `lazy val h` reference. Annotating the cast (`.asInstanceOf[Nothing]`)
   or eliminating the use of `h` altogether also suppresses it.
4. The `inline given derived[I: Mirror.ProductOf, O: Mirror.ProductOf]`
   recursion, with at least one identity-given (`given identity[I]:
   Hammer[I, I]`) in scope as an alternative.
5. **Separate compilation** of `Lib` and `Test`. Single-unit
   compilation does not trigger.

## Source

* `lib/src/main/scala/Lib.scala` — 60 LoC; the auto-derivation library.
* `test/src/main/scala/Test.scala` — 13 LoC; four nested case classes
  + a `summon[Hammer[C, D]]`.

## Background

Original community-build3 failure (test that triggered this):

> `src/test/scala/com/melvinlow/hammer/HammerSpec.scala:27`
> ("should hammer nested fields")

The previous closed regression
[`scala/scala3#22585`](https://github.com/scala/scala3/issues/22585) on
the same project had a different shape (`Found: ?1.I, Required: Nothing`,
fixed by PR #23438). The current shape, `auto.given_X does not match`
with a contradictory hint to import the same given via its `internal`
name, is a distinct issue.
