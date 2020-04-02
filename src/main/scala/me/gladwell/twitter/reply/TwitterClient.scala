package me.gladwell.twitter.reply

import cats.effect._
import cats.implicits._
import fs2._
import pureconfig._
import pureconfig.module.catseffect.syntax._
import pureconfig.generic.auto._
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

import scala.jdk.CollectionConverters._

trait TwitterClient[F[_]] { self: Logging[F] =>

  private case class Configuration(
    apiKey: String,
    secretKey: String,
    accessToken: String,
    accessTokenSecret: String
  )

  case class Status private(
    id: Long,
    user: String,
    text: String,
    replied: Boolean
  )

  object Status {
    def apply(status: twitter4j.Status, replied: Boolean): Status =
      Status(
        id      = status.getId,
        user    = status.getUser.getScreenName,
        text    = status.getText,
        replied = replied
      )
  }

  private def client()(implicit F: Sync[F]): F[Twitter] =
    for {
      config <- ConfigSource.default.at("twitter").loadF[F, Configuration]

      cb = new ConfigurationBuilder()
        .setDebugEnabled(true)
        .setOAuthConsumerKey(config.apiKey)
        .setOAuthConsumerSecret(config.secretKey)
        .setOAuthAccessToken(config.accessToken)
        .setOAuthAccessTokenSecret(config.accessTokenSecret)

      tf = new TwitterFactory(cb.build)
      twitter = tf.getInstance
    } yield twitter

  private def fetchMentions(twitter: Twitter)(implicit F: Sync[F]): F[Seq[Status]] =
    F.delay{
      twitter
        .getMentionsTimeline()
        .asScala
        .toSeq
        .map{ mention =>
          val query   = new Query(s"to:${mention.getUser.getScreenName} since_id:${mention.getId}")
          val replies = twitter.search(query).getTweets.asScala.toSeq

          val replied =
            replies
              .map{ _.getUser.getScreenName }
              .contains(twitter.getScreenName)

          Status(mention, replied)
        }
    }

  def mentions()(implicit F: Sync[F]): Stream[F, Status] =
    Stream.evalSeq {
      for {
        logger   <- loggerF()
        _        <- logger.debug("searching for mentions")
        twitter  <- client()
        mentions <- fetchMentions(twitter)
        _        <- logger.debug(s"found ${mentions.size} mentions")
      } yield mentions
    }

  def replyTo(status: Status, message: String)(implicit F: Sync[F]): Stream[F, Unit] =
    Stream.eval {
      for {
        logger  <- loggerF()
        twitter <- client()
        _       <- logger.info(s"replying to status=[${status.id}] from user=[@${status.user}] with message=[$message]")
        _       <- F.delay {
          val reply = new StatusUpdate(s"@${status.user} $message").inReplyToStatusId(status.id)
          twitter.updateStatus(reply)
        }
      } yield ()
    }

}
