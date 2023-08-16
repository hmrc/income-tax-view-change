/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers


import akka.util.Timeout
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.Span
import play.api.libs.json.{Format, Json}
import play.api.mvc.Result
import utils.TestSupport
import play.api.test.Helpers._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt


class ControllerBaseSpec extends TestSupport {


  def checkStatusOf(result: Future[Result])(expectedStatus: Int): Unit = {
    s"return status ($expectedStatus)" in {
      status(result) shouldBe expectedStatus
    }
  }

  def checkContentTypeOf(result: Future[Result])(expectedContentType: String): Unit = {
    s"Content Type of result should be $expectedContentType" in {
      contentType(result) shouldBe Some(expectedContentType)
    }
  }

  def checkJsonBodyOf[A](result: Future[Result])(expectedBody: A)(implicit format: Format[A]): Unit = {
    s"return the response body $expectedBody" in {
      contentAsJson(result) shouldBe Json.toJson(expectedBody)
    }
  }
}
