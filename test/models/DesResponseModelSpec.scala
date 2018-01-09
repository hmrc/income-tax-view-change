/*
 * Copyright 2018 HM Revenue & Customs
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

package models

import org.scalatest.Matchers
import play.api.libs.json._
import assets.TestConstants.BusinessDetails._
import assets.TestConstants._
import play.api.http.Status
import utils.TestSupport

class DesResponseModelSpec extends TestSupport with Matchers {

  "The DesResponseModel" should {
    "read from Json" in {
      Json.fromJson[DesBusinessDetails](testBusinessModelJson) shouldBe JsSuccess(desBusinessResponse(testBusinessModel))
    }
  }

  it should {
    "succeed" when {
      "the minimum amount of fields are returned" in {
        val res = desBusinessResponse(testMinimumBusinessModel)
        val tstJson = Json.parse(
          s"""{
              |"safeId":"XAIT12345678908",
              |"nino":"$testNino",
              |"mtdbsa":"$mtdRef",
              |"propertyIncome":false,
              |"businessData": [
              |{
              | "incomeSourceId": "111111111111111",
              | "accountingPeriodStartDate": "2017-06-01",
              | "accountingPeriodEndDate": "2018-05-31"
              |}
             |]
            |}
          """.stripMargin
        )
        Json.fromJson[DesBusinessDetails](tstJson) shouldBe JsSuccess(res)
      }
    }
  }

  "The DesResponseErrorModel" should {
    "have the correct status code in the model" in {
      testDesResponseError.status shouldBe Status.INTERNAL_SERVER_ERROR
    }
    "have the correct Error Message" in {
      testDesResponseError.reason shouldBe "Dummy error message"
    }
  }
}