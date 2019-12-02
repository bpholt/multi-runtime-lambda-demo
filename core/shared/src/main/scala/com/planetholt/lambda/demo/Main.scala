package com.planetholt.lambda.demo

import cats._
import cats.effect._
import io.circe._
import cats.implicits._
import com.planetholt.lambda.demo.model._
import com.dwolla.sns.model.messagesInRecordsTraversal

object Main {
  def resource[F[_] : Applicative](dynamoDbClient: DynamoDbClient[F]): Resource[F, Main[F]] =
    Resource.pure(new Main(dynamoDbClient))
}

class Main[F[_] : Applicative](dynamoDbClient: DynamoDbClient[F]) {
  def consumeJson(input: Json): F[Unit] =
    messagesInRecordsTraversal[Foo](input)
      .map(dynamoDbClient.put(_))
      .sequence_
}
