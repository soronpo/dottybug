## Minimized reproduction for scala/scala3#20010

Tracks https://github.com/scala/scala3/issues/20010
("class ValidatingTest has non-class parent").

### Crash

`sbt compile` crashes the Scala 3.8.3 compiler with:

```
java.lang.AssertionError: assertion failed:
  class ValidatingTest has non-class parent:
  TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object parent),ParsingTest)
  at dotty.tools.dotc.core.SymDenotations$ClassDenotation.traverse$1(SymDenotations.scala:2059)
  at dotty.tools.dotc.core.SymDenotations$ClassDenotation.computeBaseData(SymDenotations.scala:2064)
  ...
  at dotty.tools.dotc.typer.Namer$ClassCompleter.checkedParentType$1(Namer.scala:1677)
  at dotty.tools.dotc.typer.Namer$ClassCompleter.completeInCreationContext(Namer.scala:1768)
```

### Trigger

The build has three modules:

* `libA` defines `parent.ParsingTest` (a trait).
* `libB` defines `child.ValidatingTest extends parent.ParsingTest` and is
  compiled against `libA`.
* The `root` project extends `child.ValidatingTest` but its classpath is
  scrubbed of `libA`'s outputs, so `parent.ParsingTest` is unresolvable
  while the compiler completes `ValidatingTest`.

This mirrors what happens in the wild when a dependency moves or removes a
type but downstream artifacts keep referring to the old location. In the
original report, `riddl-testkit 0.40.0`'s `ValidatingTest` extends
`com.ossuminc.riddl.language.parsing.ParsingTest`, which no longer exists
in `riddl-language 0.40.0`'s jar.

Instead of failing with a clean "class not found" error, the compiler
creates a stub whose prefix is a chain of synthetic `TermRef`s to the
surrounding package objects, then asserts because a class parent must be
a `TypeRef` to a real class symbol.

### Reproducing from riddl-hugo

The crash still happens when building the upstream project against
Scala 3.8.3:

```
git clone https://github.com/ossuminc/riddl-hugo
cd riddl-hugo
# (bump project/build.properties to sbt 1.10.7 if needed)
GITHUB_TOKEN=dummy sbt 'set every scalaVersion := "3.8.3"' hugo/Test/compile
```
