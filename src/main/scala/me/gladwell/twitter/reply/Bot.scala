package me.gladwell.twitter.reply

import fs2.Stream
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Bot extends IOApp with Logging[IO] with TwitterClient[IO] with DeckardClient[IO] {

  private def isAskingQuestion(mention: Status): Boolean =
    ".*(who\\s*am\\s*i).*".r.matches(mention.text.toLowerCase)

  private val fetchMentions: Stream[IO, Status] =
    for {
      _       <- log("starting reply-guy")
      mention <- mentions()
    } yield mention

  private val filters: Stream[IO, Status] =
    fetchMentions
      .filter(isAskingQuestion)
      .filter{ !_.replied }

  private val stream: Stream[IO, Unit] =
    for {
      mention <- filters
      message <- rollOnTable("grislyeye/lofacharacters")
      _       <- log(s"replying to status=[${mention.id}] from user=[@${mention.user}] with message=[$message]")
      _       <- replyTo(mention, message)
      _       <- log("finished reply-guy")
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    stream
      .compile
      .drain
      .as(ExitCode.Success)

}
