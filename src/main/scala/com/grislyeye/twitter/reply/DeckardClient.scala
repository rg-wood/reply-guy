package com.grislyeye.twitter.reply

import java.net.URI

import cats.effect._
import cats.implicits._
import fs2.Stream
import org.http4s._
import org.http4s.client.blaze._
import org.http4s.Method.POST
import microtesia._
import microtesia.formats._
import org.http4s.client.middleware.RetryPolicy.exponentialBackoff
import org.http4s.client.middleware.{Retry, RetryPolicy}
import pureconfig._
import pureconfig.module.catseffect.syntax._
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration._

trait DeckardClient[F[_]] { self: Logging[F] =>

  private case class Configuration(uri: String)

  private object NoRolls extends RuntimeException("no rolls found")

  private case class Roll(description: String)

  private val rollItemType = new URI("http://grislyeye.com/deckard/microdata/roll")

  private val retryPolicy = RetryPolicy[F](exponentialBackoff(30 seconds, 3))

  def rollOnTable(table: String)(implicit F: ConcurrentEffect[F], timer: Timer[F]): Stream[F, String] =
    Stream.eval {
      BlazeClientBuilder[F](global).resource.map(Retry(retryPolicy)).use { client =>
        for {
          logger    <- loggerF()
          _         <- logger.debug(s"rolling for table=[$table]")
          config    <- ConfigSource.default.at("deckard").loadF[F, Configuration]
          uri       <- Uri.fromString(s"${config.uri}/$table/rolls").liftTo[F]
          html      <- client.expect[String](Request[F](POST, uri))
          microdata <- parseMicrodata(html).liftTo[F]
          item      <- microdata.rootItems(rollItemType).headOption.liftTo[F](NoRolls)
          roll      <- item.convertTo[Roll].liftTo[F]
        } yield roll.description
      }
    }

}
