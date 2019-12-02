package com.planetholt.lambda.demo

import cats._

trait DynamoDbClient[F[_]] {
  def put[A : Show](a: A): F[Unit]
}

object DynamoDbClient extends DynamoDbPlatform
