package com.planetholt.lambda.demo

import cats.effect._
import cats.implicits._
import com.dwolla.lambda.IOLambda
import io.circe.scalajs._
import typings.awsDashLambda.awsDashLambdaMod.Context

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

object Handler extends IOLambda[js.Any, Unit] {
  def handleRequest(a: js.Any, context: Context): IO[Unit] =
    convertJsToJson(a).liftTo[IO].flatMap { json =>
      (DynamoDbClient.resource[IO] >>= Main.resource[IO]).use(_.consumeJson(json))
    }

  @JSExportTopLevel("handleJson")
  val _ = handler
}
