package com.planetholt.lambda.demo

import cats._
import cats.effect._
import cats.implicits._
import typings.atAwsDashSdkClientDashDynamodbDashNode.dynamoDBMod.DynamoDB
import typings.atAwsDashSdkClientDashDynamodbDashNode.typesPutItemInputMod.PutItemInput
import typings.atAwsDashSdkClientDashDynamodbDashNode.typesPutItemOutputMod.PutItemOutput
import typings.atAwsDashSdkClientDashDynamodbDashNode.typesUnderscoreAttributeValueMod._AttributeValue
import DynamoDbClientJsImpl._
import org.scalablytyped.runtime.StringDictionary

import scala.concurrent.ExecutionContext.Implicits.global

trait DynamoDbPlatform {
  private def acquire[F[_] : Sync]: F[DynamoDB] =
    Sync[F].delay(new DynamoDB())

  def resource[F[_] : ConcurrentEffect]: Resource[F, DynamoDbClient[F]] =
    Resource.liftF(acquire[F])
      .map(new DynamoDbClientJsImpl(_))
}

private class DynamoDbClientJsImpl[F[_] : ConcurrentEffect](client: DynamoDB) extends DynamoDbClient[F] {
  override def put[A: Show](a: A): F[Unit] =
    Async[F].asyncF[PutItemOutput] { cb =>
      Sync[F].delay {
        val items = StringDictionary[_AttributeValue]("key" -> a)
        val input = PutItemInput(Item = items, TableName = "multi-runtime-lambda-demo")

        client.putItem(input)
          .toFuture
          .onComplete(t => cb(t.toEither))
      }
    }.void
}

object DynamoDbClientJsImpl {
  implicit def showableToAttributeValue[A: Show](a: A): _AttributeValue =
    _AttributeValue(S = Show[A].show(a))
}
