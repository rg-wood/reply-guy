package me.gladwell.twitter.reply

import cats.effect.Sync
import fs2._

trait Logging[F[_]] {

  def log(message: String)(implicit F: Sync[F]): Stream[F, Unit] =
    Stream.eval(F.delay { println(s"[info] $message") })

}
