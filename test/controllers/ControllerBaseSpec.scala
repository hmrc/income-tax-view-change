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

import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Result}
import play.api.test.FakeRequest
import utils.TestSupport


class ControllerBaseSpec extends TestSupport {

  def fakeRequestPut(payload: JsValue): FakeRequest[AnyContentAsJson] = FakeRequest("PUT", "/")
    .withJsonBody {
      payload
    }

  // same set of methods to assert test condition using Future result/assuming Future is complete
  def checkContentTypeOf(result: Result)(expectedContentType: String): Unit = {
    s"Content Type of result should be $expectedContentType" in {
      result.body.contentType shouldBe Some(expectedContentType)
    }
  }

  def checkStatusOf(result: Result)(expectedStatus: Int): Unit = {
    s"return status ($expectedStatus)" in {
      result.header.status shouldBe expectedStatus
    }
  }

  def checkJsonBodyOf[A](result: Result)(expectedBody: A)(implicit format: Format[A]): Unit = {
    s"return the response body $expectedBody" in {
      val data = result.body.consumeData
      val dataString: String = data.futureValue.decodeString("utf-8")
      val jsonResult = Json.parse(dataString)
      jsonResult shouldBe Json.toJson(expectedBody)
    }
  }
}
