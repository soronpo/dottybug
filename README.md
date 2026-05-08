# Minimized repro: lowmelvin/hammer-scala (community-build3 2026-05-01)

Standalone branch isolating the COMPILER failure of
`lowmelvin/hammer-scala` reported on Scala
`3.9.0-RC1-bin-20260501-0c8c581-NIGHTLY` (Scala 3 community-build3
nightly, 2026-05-01).

## Reproduce

```
sbt compile
```

Pinned to `3.7.4` because that's the earliest stable that already
exhibits the bug — no nightly required.

## Source

`src/main/scala/Repro.scala` (45 LoC; ~18 LoC of meaningful code).

## Symptom

```
-- [E172] Type Error: Repro.scala:45:44 --
   No given instance of type Hammer[D, given_HasT.T] was found.
   I found:
       Lib.derived[D, B](given_HasT)
   But given instance derived in object Lib does not match
   type Hammer[D, given_HasT.T].
```

Dealiasing `given_HasT.T = B` (per the `given HasT with { type T = B }`
in scope), the candidate `Lib.derived[D, B]` *does* have the required
type `Hammer[D, B]` = `Hammer[D, given_HasT.T]`. The compiler
nonetheless rejects it.

## Bisection notes

Two separate observations made while reducing this:

1. **Unminimized hammer-scala** (the actual community-build3 source —
   `src/test/scala/com/melvinlow/hammer/HammerSpec.scala`) compiled
   on a sbt project against:
   - 3.6.4 → **passes** (matches the last-good run on community-build3).
   - 3.7.4 → **fails** with the same error shape.
   So at the unreduced level, the regression entered between 3.6.4
   and 3.7.x.
2. **This minimized repro** reproduces the same error shape against
   3.6.4, 3.7.4, and 3.9.0-RC1-bin-SNAPSHOT. So this minimization is
   *broader* than the precise community-build3 regression and likely
   surfaces a long-standing limitation. The unreduced project hides it
   on 3.6.4 via an extra layer of `summonFrom` + a quoted-macro step.

## Original failure

- Job:
  https://github.com/VirtusLab/community-build3/actions/runs/25237182777/job/74005935133
- Source line:
  https://github.com/lowmelvin/hammer-scala/blob/master/src/test/scala/com/melvinlow/hammer/HammerSpec.scala#L27

## Adjacent prior bugs (closed; similar signature)

- scala/scala3#25417 / scala/scala3#25427 — same "I found … but does
  not match" shape on **opaque-type** givens. Closed by PR #25448.
  The fix did not cover this **type-member**-on-trait case.
- scala/scala3#22585 — earlier hammer-scala regression on 3.6.4
  (different root cause: `lazy val` inside an inline body).
