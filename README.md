## Scala3 minimized bugs projects

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, and get the reported failures.

### Reproduction for scala/scala3#21383

This branch reproduces the assertion failure
`assertion failed: position not set for new lib.SqlName # -1 of class dotty.tools.dotc.ast.Trees$Select`
during incremental recompilation.

Steps:

```
sbt clean
sbt compile                              # succeeds
printf '\n//\n' >> src/main/scala/app/Test.scala
sbt compile                              # fails with the assertion
```

The original report (#21383) used the `magnum` library; this branch reproduces
the same crash with only the standard library, so no external dependency is
needed.
