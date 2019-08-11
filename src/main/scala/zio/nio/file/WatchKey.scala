package zio.nio.file

import java.io.IOException
import java.nio.file.{FileSystem => JFileSystem, WatchEvent, WatchKey => JWatchKey, WatchService => JWatchService}

import zio.blocking.Blocking

import scala.collection.JavaConverters._
import zio.{IO, UIO, ZIO}

trait Watchable {

  def register(watcher: WatchService, )
}

final class WatchKey private (private val javaKey: JWatchKey) {

  def isValid: UIO[Boolean] = UIO.effectTotal(javaKey.isValid)

  def pollEvents: UIO[List[WatchEvent[_]]] = UIO.effectTotal(javaKey.pollEvents().asScala.toList)

  def reset: UIO[Boolean] = UIO.effectTotal(javaKey.reset())

  def cancel: UIO[Unit] = UIO.effectTotal(javaKey.cancel())

}

final class WatchService private (private val javaWatchService: JWatchService) {


}

object WatchService {

  def forDefaultFileSystem: ZIO[Blocking, Exception, WatchService] = FileSystem.default.newWatchService

  def fromJava(javaWatchService: JWatchService): WatchService = new WatchService(javaWatchService)

}