package me.gladwell.twitter.reply

import fs2._
import cats.effect._
import cats.implicits._

import scala.concurrent.duration._

object Bot extends IOApp with Logging[IO] with TwitterClient[IO] with DeckardClient[IO] {

  private def isAskingQuestion(mention: Status): Boolean =
    ".*(who\\s*am\\s*i).*".r.matches(mention.text.toLowerCase)

  private val startUp: Stream[IO, Unit] =
    for {
      logger <- loggerSF()
      _      <- logger.info(s"starting ${BuildInfo.name} (v${BuildInfo.version})")
    } yield ()

  private val stream: Stream[IO, Unit] =
    for {
      mention <- mentions().filter(isAskingQuestion).filter{ !_.replied }
      message <- rollOnTable("grislyeye/lofacharacters")
      _       <- replyTo(mention, message)
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    (startUp >> Stream.awakeEvery[IO](30 minutes) >> stream)
      .compile
      .drain
      .as(ExitCode.Success)

}
