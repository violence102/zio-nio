package zio.nio.file

import java.io.InputStream
import java.nio.file.CopyOption

import zio.{Chunk, IO, ZIO, ZManaged}
import zio.blocking._
import zio.stream.{ZStream, ZStreamChunk}


object Files {

  def toInputStream(in: ZStreamChunk[Blocking, Exception, Byte]): ZManaged[Blocking, Exception, InputStream] = {

    val acquire = for {
      // create atomic buffer of some kind, perhaps queue?
    }

    def release(in: InputStream): ZIO[Blocking, Nothing, Unit] = ZIO.effect(effectBlocking(in.close())).ignore

    ZManaged.make(acquire, release)
  }

  def copy(in: ZStreamChunk[Blocking, Exception, Byte], target: Path, options: CopyOption*): ZIO[Blocking, Exception, Long] =

}
