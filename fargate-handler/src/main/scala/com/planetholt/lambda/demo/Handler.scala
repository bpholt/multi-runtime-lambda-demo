package com.planetholt.lambda.demo

import cats.effect._
import cats.implicits._
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze._
import org.http4s.implicits._
import org.http4s.server._

object Handler extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    HttpServer.resource[IO]().use(_ => IO.never)
}

object HttpServer {
  private def server[F[_] : ConcurrentEffect : Timer](host: String,
                                                      port: Int): HttpApp[F] => Resource[F, Server[F]] =
    BlazeServerBuilder[F]
      .bindHttp(port, host)
      .withHttpApp(_)
      .resource

  def resource[F[_] : ConcurrentEffect : Timer](host: String = defaults.Host,
                                                port: Int = defaults.HttpPort,
                                                ): Resource[F, Server[F]] =
    DynamoDbClient.resource[F] >>= Main.resource[F] >>= AcceptPostedJson.resource[F] >>= server(host, port)
}

object AcceptPostedJson {
  def resource[F[_] : Sync](main: Main[F]): Resource[F, HttpApp[F]] = {
    val dsl = Http4sDsl[F]
    import dsl._

    Resource.pure(HttpRoutes.of[F] {
      case req @ POST -> Root =>
        (req.as[Json] >>= main.consumeJson) >> NoContent()
    }.orNotFound)
  }
}
