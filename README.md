## Scala3 minimized bugs projects

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, and get the reported failures.

### Issue

Minimized reproduction of https://github.com/scala/scala3/issues/24719

Compiler crash `java.lang.AssertionError: assertion failed` in
`dotty.tools.dotc.core.Annotations$LazyAnnotation.tree` triggered when
source files redefine `scala.Tuple1` and `scala.Tuple22` while the
scala3-library (which also defines them) is on the classpath.

Originally hit when compiling the `stdlib213` community project:
https://github.com/dotty-staging/scala213/tree/3f6bdaeafde17d790023cc3f299b81eaaf876ca3

Affected compiler versions: 3.8.0-RC3, 3.8.1-RC1-bin-20251209-07883c1-NIGHTLY
(not reproducible in 3.3.7 and 3.7.4).
