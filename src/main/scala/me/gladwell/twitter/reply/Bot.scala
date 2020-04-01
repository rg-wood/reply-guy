package me.gladwell.twitter.reply

import fs2._
import cats.effect._
import cats.implicits._

import scala.concurrent.duration._

object Bot extends IOApp with Logging[IO] with TwitterClient[IO] with DeckardClient[IO] {

  private def isAskingQuestion(mention: Status): Boolean =
    ".*(who\\s*am\\s*i).*".r.matches(mention.text.toLowerCase)

  private val fetchMentions: Stream[IO, Status] =
    for {
      _       <- log("starting reply-guy")
      mention <- mentions()
    } yield mention

  private val fetchAndFilterMentions: Stream[IO, Status] =
    fetchMentions
      .filter(isAskingQuestion)
      .filter{ !_.replied }

  private val stream: Stream[IO, Unit] =
    for {
      mention <- fetchAndFilterMentions
      message <- rollOnTable("grislyeye/lofacharacters")
      _       <- log(s"replying to status=[${mention.id}] from user=[@${mention.user}] with message=[$message]")
      _       <- replyTo(mention, message)
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    (Stream.awakeEvery[IO](10 minutes) >> stream)
      .compile
      .drain
      .as(ExitCode.Success)

}
