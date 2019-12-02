/*
 * The MIT License (MIT)
 * Copyright © 2019 Dwolla
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * https://github.com/Dwolla/codecommit-merge-on-comment/blob/a5ee2b49a230165512ea35892309dfbe6600bfd8/src/main/scala/com/dwolla/sns/model/package.scala
 */

package com.dwolla.sns

import io.circe._
import io.circe.optics.JsonPath.root
import io.circe.optics.UnsafeOptics
import monocle._

package object model {
  val snsMessage: Traversal[Json, String] = root.Records.each.Sns.Message.as[String]

  private val snsMessageDetail: Optional[Json, Json] = root.detail.json

  private val parseStringAsJson: Prism[String, Json] =
    Prism[String, Json](parser.parse(_).toOption)(Printer.noSpaces.print)

  def messagesInRecordsTraversal[A: Decoder : Encoder](json: Json): List[A] =
    snsMessage
      .composePrism(parseStringAsJson)
      .composeOptional(snsMessageDetail)
      .composePrism(UnsafeOptics.parse[A])
      .getAll(json)

}
