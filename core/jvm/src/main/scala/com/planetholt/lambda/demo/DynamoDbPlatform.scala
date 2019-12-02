package com.planetholt.lambda.demo

import cats._
import cats.effect._
import com.dwolla.fs2aws.AwsEval._
import com.planetholt.lambda.demo.DynamoDbClientJvmImpl._
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model._

import scala.jdk.CollectionConverters._

trait DynamoDbPlatform {
  private def acquire[F[_] : Sync]: F[DynamoDbAsyncClient] =
    Sync[F].delay(DynamoDbAsyncClient.builder().build())

  def resource[F[_] : ConcurrentEffect]: Resource[F, DynamoDbClient[F]] =
    Resource.fromAutoCloseable(acquire[F])
      .map(new DynamoDbClientJvmImpl(_))
}

private class DynamoDbClientJvmImpl[F[_] : ConcurrentEffect](client: DynamoDbAsyncClient) extends DynamoDbClient[F] {
  private def buildPutItemRequest[A: Show](a: A): PutItemRequest =
    PutItemRequest.builder()
      .item(Map("key" -> showableToAttributeValue(a)).asJava)
      .tableName("multi-runtime-lambda-demo")
      .build()

  private def void[A]: A => Unit = _ => ()

  override def put[A: Show](a: A): F[Unit] =
    eval[F](buildPutItemRequest(a))(client.putItem)(void)
}

object DynamoDbClientJvmImpl {
  def showableToAttributeValue[A: Show](a: A): AttributeValue =
    AttributeValue.builder().s(Show[A].show(a)).build()
}
