package com.grislyeye.twitter.reply

import java.net.URI

import cats.effect._
import cats.implicits._
import fs2._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import org.flywaydb.core.Flyway
import pureconfig._
import pureconfig.module.catseffect.syntax._
import pureconfig.generic.auto._

trait Database[F[_]] {

  implicit protected val contextShift: ContextShift[F]

  private case class Configuration(
    driver: String,
    url: URI,
    user: String,
    password: String,
    threads: Int
  )

  def migrate()(implicit F: Async[F]): Stream[F, Unit] =
    Stream.eval {
      for {
        database <- ConfigSource.default.at("database").loadF[F, Configuration]
        _ <-
          F.delay {
            Flyway.configure
              .dataSource(database.url.toString, database.user, database.password)
              .load
              .migrate()
          }
      } yield ()
    }

  private def transactor(implicit F: Async[F]): Resource[F, Transactor[F]] =
    for {
      database   <- Resource.liftF(ConfigSource.default.at("database").loadF[F, Configuration])
      threadPool <- ExecutionContexts.fixedThreadPool[F](database.threads)
    } yield Transactor.fromDriverManager[F](
      database.driver,
      database.url.toString,
      database.user,
      database.password,
      Blocker.liftExecutionContext(threadPool)
    )

  def replyNotStored(statusId: Long)(implicit F: Async[F]): F[Boolean] =
    transactor.use { xa =>
      sql"select status_id from replies where status_id = $statusId"
        .query[Long]
        .stream
        .compile
        .last
        .transact(xa)
        .map{ _.isEmpty }
    }

  def storeReply(statusId: Long)(implicit F: Async[F]): Stream[F, Unit] =
    Stream.eval {
      transactor.use { xa =>
        sql"insert into replies (status_id) values ($statusId)"
          .update
          .run
          .transact(xa)
          .void
      }
    }

}
