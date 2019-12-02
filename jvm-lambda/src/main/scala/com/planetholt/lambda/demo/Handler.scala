package com.planetholt.lambda.demo

import cats.effect._
import cats.implicits._
import com.dwolla.lambda.IOLambda
import io.circe._

object Handler extends IOLambda[Json, Unit] {
  override def handleRequest(blocker: Blocker)
                            (a: Json): IO[Option[Unit]] =
    (DynamoDbClient.resource[IO] >>= Main.resource[IO]).use {
      _.consumeJson(a)
      .map(_.pure[Option])
    }

}
