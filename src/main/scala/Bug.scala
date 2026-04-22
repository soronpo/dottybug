// Dependency-free minimization of https://github.com/scala/scala3/issues/24537.
// Compiling this file causes the compiler to hang indefinitely in a
// non-terminating TypeComparer loop.
import io.github.iltotore.iron.*

trait Path
trait Stream[F[_], A]:
  def evalMapFilter[F2[_], O2](f: A => F2[Option[O2]]): Stream[F2, O2]

final class A
final class D
type AD = D & A

extension (p: Path) def asD[F[_]]: F[Option[Path :| D]] = ???
extension (p: Path :| D) def ls[F[_]]: Stream[F, Path] = ???

extension (p: Path :| AD)
  def lsDA[F[_]]: Stream[F, Path :| AD] = p.ls.evalMapFilter(_.asD)
