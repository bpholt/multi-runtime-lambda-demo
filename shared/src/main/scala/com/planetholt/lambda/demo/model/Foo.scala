package com.planetholt.lambda.demo.model

import cats.Show
import io.circe._
import io.circe.generic.semiauto._

case class Foo(foo: String)

object Foo {
  implicit val fooCodec: Codec[Foo] = deriveCodec

  implicit val fooShow: Show[Foo] = _.foo
}
