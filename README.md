# OpenCB Mill build failures — fix-this handoff

You're being asked to fix the **Mill build failures** in `VirtusLab/community-build3`
(the Open Community Build harness for Scala 3). These are **NOT** Scala 3 compiler
bugs — the compiler is correctly flagging real API-shape mismatches in
community-build3's own helper code. The 23 BUILD:mill failures in the
2026-05-01 weekly run all stem from community-build3 having drifted behind Mill
1.x's API.

## Scope

- **Repo**: https://github.com/VirtusLab/community-build3 (HEAD `e4af9624`,
  2026-05-08).
- **Files to change** (only): inside `project-builder/mill/` and
  `project-builder/prepare-scripts/com-lihaoyi/mill`.
- **Out of scope**: `scala/scala3` source. Do not patch the compiler. Do not
  touch projects' own build files.
- **Push target**: User has no write access to VirtusLab/community-build3 — fork
  to `soronpo/community-build3` (or your equivalent), branch
  `fix/mill-1.x-api`, push there, ask before opening an upstream PR.
- **Reference dashboard**: https://virtuslab.github.io/community-build3 — Weekly
  A `25237415541`, Weekly B `25237182777`. Failing nightly:
  `3.9.0-RC1-bin-20260501-0c8c581-NIGHTLY`.

## Three failure patterns

### Pattern A — `Result.Failure` shape mismatch (≈18 of 23)

**File**: `project-builder/mill/MillCommunityBuild.scala`
**Line**: 170 (the `case Result.Failure(error) =>` arm of `eval`)

```scala
def eval[T](task: Task.Named[T]): EvalResult[T] = {
  val evalStart = System.currentTimeMillis()
  val result = tryEval(task)
  val tookMillis = (System.currentTimeMillis() - evalStart).toInt
  result match {
    case Result.Success(v) =>
      ctx.log.info(s"Successfully evaluated $task")
      EvalResult.Value(v, evalTime = tookMillis)
    case Result.Failure(error) =>                      // <-- LINE 170
      ctx.log.error(s"Failed to evaluated $task: ${error}")
      EvalResult.Failure(EvaluationFailure(error) :: Nil, evalTime = tookMillis)
  }
}
```

**Compiler error** (Scala 3.9.0-nightly, against Mill 1.0.6+):
```
[error] mill-build/src/millbuild/MillCommunityBuild.scala:170:28
[error]         case Result.Failure(error) =>
[error]                            ^^^^^^^
[error] Wrong number of argument patterns for mill.api.Result.Failure;
[error] expected: (String, java.nio.file.Path, Int,
[error]            Seq[mill.api.daemon.Result².Failure.ExceptionInfo],
[error]            String,
[error]            Option[mill.api.daemon.Result².Failure])
```

**Cause**: Mill 1.x changed `mill.api.Result.Failure` from a 1-arg `case class
Failure(reason: String)` to a 6-arg case class carrying execution context
(file, exit code, exception info, label, parent failure). The single-arg
extractor no longer compiles.

**Fix sketch** (verify against the live Mill API; the parameter names below are
from the compiler error message, not direct source inspection):

```scala
case Result.Failure(reason, path, exitCode, exceptions, label, parent) =>
  ctx.log.error(s"Failed to evaluate $task: $reason")
  EvalResult.Failure(EvaluationFailure(reason) :: Nil, evalTime = tookMillis)
```

If `EvaluationFailure` needs a richer payload, thread the additional fields
through. If only the message is wanted, a wildcard binding works:

```scala
case f: Result.Failure =>
  ctx.log.error(s"Failed to evaluate $task: ${f.reason}")  // adjust accessor name
  EvalResult.Failure(EvaluationFailure(f.reason) :: Nil, evalTime = tookMillis)
```

Confirm the public field/accessor names by reading the current Mill source:
`com-lihaoyi/mill` repo, package `mill.api`, `Result.scala`. Mill 1.0.6 is the
version pulled by most failing projects (see logs).

**Affected projects** (BUILD:mill, run A unless noted):
- alexarchambault/case-app
- alexarchambault/mill-checks (run B)
- almond-sh/almond
- bishabosha/scala-object-notation
- com-lihaoyi/castor
- com-lihaoyi/requests-scala
- com-lihaoyi/unroll
- com-lihaoyi/upickle
- com-lihaoyi/utest
- databricks/sjsonnet
- disneystreaming/alloy
- jam01/sjsonnet (run B)
- lewisjkl/header
- lolgab/layers-dotty-plugin (run B)
- marmaladesky/mill-explicit-dependencies (run B)
- quafadas/live-server-scala-cli-js
- quafadas/vecxt
- torrentdam/bittorrent
- virtuslab/scala-cli
- yoohaemin/explicitly-inferred (run B)

### Pattern B — `defaultTask` diamond override

**Affected**: `reactivecore/datacomparison` (run B job 74005935349). Possibly
others surface this once Pattern A is fixed.

**Compiler error**:
```
[error] -- [E164] /opencb/repo/build.mill:51:7
[error] 51 │trait SharedTestModule extends TestModule.ScalaTest
[error]    │       with MillCommunityBuild.CommunityBuildCoursierModule
[error]    │       { override def scalaTestVersion = "3.2.19" }
[error]    │      ^
[error]    │error overriding method defaultTask in trait TestModule of type (): String;
[error]    │  method defaultTask² in trait JavaModule of type (): String
[error]    │trait SharedTestModule inherits conflicting members:
[error]    │  method defaultTask in trait TestModule of type (): String  and
[error]    │  method defaultTask in trait JavaModule of type (): String
[error]    │(Note: this can be resolved by declaring an override in trait SharedTestModule.)
```

**Cause**: Mill 1.x added/relocated `defaultTask` so it's inherited from both
`TestModule` and `JavaModule`. community-build3's
`MillCommunityBuild.CommunityBuildCoursierModule` mixes alongside
`TestModule.ScalaTest` in user-side downstream traits (e.g. the
project-template `SharedTestModule`), and the diamond doesn't auto-resolve.

**Fix sketch**: Override `defaultTask` (and any sibling members listed in the
"other members with override errors are: …" line of the error) inside
`MillCommunityBuild.CommunityBuildCoursierModule`, delegating to whichever
parent should win:

```scala
trait CommunityBuildCoursierModule extends CoursierModule {
  override def defaultTask: String = super[TestModule].defaultTask
  // and similarly for `mandatoryMvnDeps` etc. as flagged
}
```

Adjust the `super[…]` qualifier per which trait the project-template extends.
Read the full error block in the failing log for the complete list of
conflicting members (the snippet above truncates after `defaultTask`).

### Pattern C — `com-lihaoyi/mill` prepare-script reads `build.sc`

**Affected**: `com-lihaoyi/mill` (job 74008856656).

**File**: `project-builder/prepare-scripts/com-lihaoyi/mill` (current source
included verbatim below).

**Runtime error**:
```
Exception in thread "main" java.nio.file.NoSuchFileException:
  /tmp/mill-moduledefs-…/build.sc
  at os.read.bytes …
```

**Cause**: The script clones `com-lihaoyi/mill-moduledefs` and reads
`build.sc`, but mill-moduledefs has migrated to Mill 1.x — its build file is
now `build.mill` (verified from the live tree: `build.mill`, `mill`,
`moduledefs/…`, no `build.sc`). The text-replacement patterns the script
applies (`Cross[ModuleDefsCross]`, `Cross[PluginCross]`, `-Yexplicit-nulls`,
`-java-output-version`) also no longer exist in `build.mill`'s shape.

**Current script body**:
```bash
#!/usr/bin/env -S scala-cli shebang -S 3
//> using toolkit default

val projectDir = sys.env.get("OPENCB_PROJECT_DIR").map(os.Path(_))
  .getOrElse(sys.error("no OPENCB_PROJECT_DIR env"))
os.write.append(projectDir / ".mill-jvm-opts", "-XX:CompressedClassSpaceSize=1400m")

val scalaVersion = sys.env.get("OPENCB_SCALA_VERSION")
  .getOrElse(sys.error("no OPENCB_SCALA_VERSION env"))
val minJDKVersion = scalaVersion.split('.').take(2).map(_.toInt) match {
  case Array(3, minor) if minor >= 8 => 17
  case _ => 8
}

val millDefsVersion = os.read
  .lines(projectDir / "mill-build" / "src" / "millbuild" / "Deps.scala")
  .map(_.trim)
  .collectFirst:
    case s"""val millModuledefsVersion = "$version"""" => version
  .getOrElse(sys.error("Failed to resolve millModuleDefs version"))

val repositoryDir = os.temp.dir(prefix = "mill-moduledefs-")
os.proc("git", "clone", "https://github.com/com-lihaoyi/mill-moduledefs",
        repositoryDir, "-b", millDefsVersion).call(check = true)

val updatedBuild = os.read(repositoryDir / "build.sc")
  .replaceAll(raw"Cross\[ModuleDefsCross]\(.*\)", s"""Cross[ModuleDefsCross](Seq("3.7.1"))""")
  .replaceAll(raw"Cross\[PluginCross]\(.*\)",     s"""Cross[PluginCross](Seq("$scalaVersion"))""")
  .replace(""""-Yexplicit-nulls",""", "")
  .replace(""""-java-output-version", "8""", s""""-java-output-version:${minJDKVersion}""")
os.write.over(repositoryDir / "build.sc", updatedBuild)
os.write.over(repositoryDir / ".mill-version", "0.12.15-2-561986")

val coursierRepositories = Seq(
  "central", "ivy2local",
  "https://repo.scala-lang.org/artifactory/maven-nightlies",
  s"https://scala3.westeurope.cloudapp.azure.com/maven2/$scalaVersion",
).mkString("|")

os.proc(
  "./mill", "--no-server",
  "-D", s"coursier.repositories=$coursierRepositories",
  "show", s"__[$scalaVersion].publishLocal",
).call(cwd = repositoryDir, check = true)
```

**What the live `build.mill` looks like** (extract from
`com-lihaoyi/mill-moduledefs@HEAD`, 2026-05):
```scala
//| mill-version: 1.1.0-RC2
//| mill-jvm-version: 11
package build

import mill.scalalib._
import mill.scalalib.publish._
import mill.api.*
import mill.*

object Settings {
  val version = "0.13.1"
  …
}
object Deps {
  val libScala2Version = "2.13.18"
  val libScala3Version = "3.7.4"
  val scalaVersionEnv = sys.env.get("SCALA_VERSION")
    .map(_.trim).filter(_.nonEmpty).…
  val scala2Versions = (0.to(17).map(v => "2.13." + v) ++ Seq(libScala2Version) ++ scalaVersionEnv.filter(_.startsWith("2.13"))).distinct
  val scala3Versions = (Seq("3.7.0",…,"3.8.0","3.8.1") ++ Seq(libScala3Version) ++ scalaVersionEnv.filter(_.startsWith("3"))).distinct
  …
}
```
Note that `build.mill` already supports a `SCALA_VERSION` env var natively and
already lists Scala 3 cross-versions. The script can simply set
`SCALA_VERSION` and skip text patching.

**Fix sketch**:
```scala
// 1. Read from build.mill, not build.sc.
val buildFile = repositoryDir / "build.mill"

// 2. Don't text-patch — the upstream `build.mill` already reads SCALA_VERSION
//    from the env (see the `scalaVersionEnv` mechanism). Pass it through:
os.proc(
  "./mill", "--no-server",
  "-D", s"coursier.repositories=$coursierRepositories",
  "show", s"__[$scalaVersion].publishLocal",
).call(
  cwd = repositoryDir,
  env = Map("SCALA_VERSION" -> scalaVersion),
  check = true
)

// 3. Pin a working .mill-version compatible with the new build.mill (it
//    declares `//| mill-version: 1.1.0-RC2`). Either drop the override, or
//    set it to the latest Mill 1.x release the rest of CB3 already uses.
```

The `Cross[ModuleDefsCross]` / `Cross[PluginCross]` / `-Yexplicit-nulls` /
`-java-output-version` substitutions are dead — those identifiers are gone
from `build.mill`. Delete those `replace`/`replaceAll` lines.

Also: the `millDefsVersion` lookup pattern
`val millModuledefsVersion = "..."` should still work against current Mill 1.x
projects' `mill-build/src/millbuild/Deps.scala` if they still expose that
constant — verify by reading any Mill 1.x project's Deps.scala (e.g.
`com-lihaoyi/mill`'s own `mill-build/src/millbuild/Deps.scala`). If the
constant has been renamed, update the regex accordingly.

## Repro / verification approach

community-build3 is GitHub-Actions-driven. Local repro of one project:

```bash
# 1. Clone the harness
git clone https://github.com/VirtusLab/community-build3
cd community-build3

# 2. Pick a small failing project, e.g. com-lihaoyi/utest. Its docker image
#    is `ghcr.io/virtuslab/scala-community-build-project-builder:jdk17-latest`.
#    The exact invocation comes from
#    `.github/actions/build-project/index.sh` — read that to see how the
#    container is launched and what env vars (PROJECT_NAME, SCALA_VERSION,
#    OPENCB_PROJECT_DIR, OPENCB_SCALA_VERSION) it expects.
#
# 3. Run a single project locally: docker run with PROJECT_NAME=<owner>/<repo>
#    SCALA_VERSION=3.9.0-RC1-bin-20260501-0c8c581-NIGHTLY pointing the
#    /opencb/ volume at your local checkout. The harness will compile
#    project-builder/mill/MillCommunityBuild.scala against Mill 1.x and
#    surface the same error as in CI.
```

To verify a patch end-to-end without a full weekly run, use the manual
workflow `Open CB: custom/manual builds A`:
```bash
gh workflow run "Open CB: custom/manual builds A" \
  -R VirtusLab/community-build3 \
  -f scala_version=3.9.0-RC1-bin-20260501-0c8c581-NIGHTLY \
  -f config=…    # consult workflow YAML for input names
```
(Confirm the input names from `.github/workflows/`.)

## Cross-checks before opening a PR

1. **Mill API confirmation**: Read
   https://github.com/com-lihaoyi/mill/blob/main/core/api/src/mill/api/Result.scala
   (or the equivalent under `core/api/daemon/`). Confirm the
   `Result.Failure` field names match the fix above. If Mill exposes a stable
   `reason: String` accessor, prefer that over destructuring.
2. **Diamond resolution**: Reproduce Pattern B with `reactivecore/datacomparison`
   to confirm the override list. The error message lists "other members with
   override errors are: method mandatoryMvnDeps" and likely more — capture
   the full list and override each.
3. **Prepare-script**: Test the rewritten script against
   `mill-moduledefs@0.13.1` (current HEAD tag). Confirm `__[<scalaVersion>].publishLocal`
   succeeds for `scalaVersion = 3.9.0-RC1-bin-20260501-0c8c581-NIGHTLY`.
4. **Search for prior reports**: I did a quick search and saw nothing in
   `VirtusLab/community-build3` issues/PRs about `Result.Failure` or
   `mill 1.0.6` (as of 2026-05-09). Re-check before duplicating effort:
   ```
   gh search issues --repo VirtusLab/community-build3 --state all "Result.Failure"
   gh search prs    --repo VirtusLab/community-build3 --state all "mill"
   ```

## Important context (so you don't get sidetracked)

- The Scala 3 nightly `3.9.0-RC1-bin-20260501-0c8c581-NIGHTLY` is the source of
  truth here. It bumps TASTy minor (28.9), which causes a separate
  *operational* cluster of failures (artifacts compiled against 3.8.x can't be
  read by 3.9.0). That cluster is the COMPILER:tasty-forward-incompatible
  one, **not** these mill failures, and is being resolved by VirtusLab
  republishing stdlib + downstream artifacts. Don't conflate the two.
- The 23 BUILD:mill failures are tracked in the parallel triage at
  `soronpo/dottybug#1` ("Scala 3.9.0 nightly community-build triage"). After
  fixing, update that issue's per-row entries from `pending` → `done`
  (community-build3 fix), or just notify the user.
- Per the standing triage policy, BUILD failures (sbt + mill) are descoped
  from the scala/scala3 triage track. This handoff exists because the user
  asked specifically why Mill is failing; you're now scoped to fix
  community-build3 only.

## TL;DR

Three independent fixes in `VirtusLab/community-build3`:

1. `project-builder/mill/MillCommunityBuild.scala:170` — update
   `case Result.Failure(error)` to the new 6-arg shape.
2. `project-builder/mill/MillCommunityBuild.scala`
   `CommunityBuildCoursierModule` — add `override def defaultTask` (and
   sibling members) to break the diamond.
3. `project-builder/prepare-scripts/com-lihaoyi/mill` — rename the
   `build.sc` reads/writes to `build.mill`, drop the dead text
   substitutions, pass `SCALA_VERSION` via env instead.

Push to a fork, open the PR against `VirtusLab/community-build3` master after
local verification.
