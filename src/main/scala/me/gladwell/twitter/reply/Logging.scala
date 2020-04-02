package me.gladwell.twitter.reply

import cats.effect.Sync
import cats.implicits._
import fs2._
import _root_.io.chrisdavenport.log4cats.Logger
import _root_.io.chrisdavenport.log4cats.slf4j.Slf4jLogger

trait Logging[F[_]] {

  private class StreamLogger(logger: Logger[F]) extends Logger[Stream[F, *]] {

    override def error(message: => String): Stream[F, Unit] = Stream.eval(logger.error(message))

    override def warn(message: => String): Stream[F, Unit] =  Stream.eval(logger.warn(message))

    override def info(message: => String): Stream[F, Unit] = Stream.eval(logger.info(message))

    override def debug(message: => String): Stream[F, Unit] = Stream.eval(logger.debug(message))

    override def trace(message: => String): Stream[F, Unit] = Stream.eval(logger.trace(message))

    override def error(t: Throwable)(message: => String): Stream[F, Unit] =
      Stream.eval(logger.error(t)(message))

    override def warn(t: Throwable)(message: => String): Stream[F, Unit] =
      Stream.eval(logger.warn(t)(message))

    override def info(t: Throwable)(message: => String): Stream[F, Unit] =
      Stream.eval(logger.info(t)(message))

    override def debug(t: Throwable)(message: => String): Stream[F, Unit] =
      Stream.eval(logger.debug(t)(message))

    override def trace(t: Throwable)(message: => String): Stream[F, Unit] =
      Stream.eval(logger.trace(t)(message))

  }

  def loggerSF()(implicit F: Sync[F]): Stream[F, Logger[Stream[F, *]]] =
    Stream.eval(loggerF().map { new StreamLogger(_) })

  def loggerF()(implicit F: Sync[F]) = Slf4jLogger.create[F]

}
