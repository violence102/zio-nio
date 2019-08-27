package zio.nio.file

import java.net.URI
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.{file => jf}

import zio.blocking.Blocking
import zio.{IO, UIO, ZIO}

import scala.collection.JavaConverters._

final class FileSystem private (private val javaFileSystem: jf.FileSystem) {

  def provider: jf.spi.FileSystemProvider = javaFileSystem.provider()

  def close: ZIO[Blocking, Exception, Unit] =
    ZIO.accessM[Blocking](_.blocking.effectBlocking(javaFileSystem.close())).refineToOrDie[Exception]

  def isOpen: UIO[Boolean] = UIO.effectTotal(javaFileSystem.isOpen())

  def isReadOnly: Boolean = javaFileSystem.isReadOnly

  def getSeparator: String = javaFileSystem.getSeparator

  def getRootDirectories: List[Path] = javaFileSystem.getRootDirectories.asScala.map(Path.fromJava).toList

  def getFileStores: List[jf.FileStore] = javaFileSystem.getFileStores.asScala.toList

  def supportedFileAttributeViews: Set[String] = javaFileSystem.supportedFileAttributeViews().asScala.toSet

  def getPath(first: String, more: String*): Path = Path.fromJava(javaFileSystem.getPath(first, more:_*))

  def getPathMatcher(syntaxAndPattern: String): jf.PathMatcher = javaFileSystem.getPathMatcher(syntaxAndPattern)

  def getUserPrincipalLookupService: UserPrincipalLookupService = javaFileSystem.getUserPrincipalLookupService

  def newWatchService: ZIO[Blocking, Exception, WatchService] =
    ZIO.accessM[Blocking](_.blocking.effectBlocking(WatchService.fromJava(javaFileSystem.newWatchService()))).refineToOrDie[Exception]

}

object FileSystem {

  def fromJava(javaFileSystem: jf.FileSystem): FileSystem = new FileSystem(javaFileSystem)

  def default: FileSystem = fromJava(jf.FileSystems.getDefault)

  def getFileSystem(uri: URI): IO[Exception, FileSystem] =
    IO.effect(fromJava(jf.FileSystems.getFileSystem(uri))).refineToOrDie[Exception]

  def newFileSystem(uri: URI, env: (String, Any)*): IO[Exception, FileSystem] =
    IO.effect(fromJava(jf.FileSystems.newFileSystem(uri, env.toMap.asJava))).refineToOrDie[Exception]

  def newFileSystem(uri: URI, env: Map[String, _], loader: ClassLoader): IO[Exception, FileSystem] =
    IO.effect(fromJava(jf.FileSystems.newFileSystem(uri, env.asJava, loader))).refineToOrDie[Exception]

  def newFileSystem(path: Path, loader: ClassLoader): IO[Exception, FileSystem] =
    IO.effect(fromJava(jf.FileSystems.newFileSystem(path.javaPath, loader))).refineToOrDie[Exception]

}