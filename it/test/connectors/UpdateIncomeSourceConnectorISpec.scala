/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors

import helpers.{ComponentSpecBase, WiremockHelper}
import models.updateIncomeSource.{UpdateIncomeSourceResponseError, UpdateIncomeSourceResponseModel}
import models.updateIncomeSource.request.UpdateIncomeSourceRequestModel
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json

class UpdateIncomeSourceConnectorISpec extends ComponentSpecBase{

  val connector: UpdateIncomeSourceConnector = app.injector.instanceOf[UpdateIncomeSourceConnector]

  val url: String = "/income-tax/business-detail/income-source"

  val request = UpdateIncomeSourceRequestModel(
    "AA000000A",
    "testId",
    None,
    None
  )


  "UpdateIncomeSourceConnector" when {
    "updateIncomeSource is called" when {
      "the response is an Ok - 200" should {
        "return a UpdateIncomeSourceResponseModel when successful" in {
          val responseBody = UpdateIncomeSourceResponseModel("01-01-2020")
          WiremockHelper.stubPut(url, OK, Json.toJson(responseBody).toString())
          val result = connector.updateIncomeSource(request)
          result.futureValue shouldBe UpdateIncomeSourceResponseModel("01-01-2020")
        }
        "return a UpdateIncomeSourceResponseError when unable to parse the data into a UpdateIncomeSourceResponseModel" in {
          val responseBody = Map("fake"->"data")
          WiremockHelper.stubPut(url, OK, Json.toJson(responseBody).toString())
          val result = connector.updateIncomeSource(request)
          result.futureValue shouldBe UpdateIncomeSourceResponseError(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing IF Update Income Source")
        }
      }
      "the response is not 200" should {
        "return a UpdateIncomeSourceResponseError" when {
          "the response is a 404 - Not Found" in {
            val responseBody = UpdateIncomeSourceResponseError(NOT_FOUND, "Not found")
            WiremockHelper.stubPut(url, NOT_FOUND, Json.toJson(responseBody).toString())
            val result = connector.updateIncomeSource(request)
            result.futureValue shouldBe UpdateIncomeSourceResponseError(NOT_FOUND, "{\"status\":404,\"reason\":\"Not found\"}")
          }
          "the response is a 500 - InternalServerError" in {
            val responseBody = UpdateIncomeSourceResponseError(INTERNAL_SERVER_ERROR, "Internal server error")
            WiremockHelper.stubPut(url, INTERNAL_SERVER_ERROR, Json.toJson(responseBody).toString())
            val result = connector.updateIncomeSource(request)
            result.futureValue shouldBe UpdateIncomeSourceResponseError(INTERNAL_SERVER_ERROR, "{\"status\":500,\"reason\":\"Internal server error\"}")
          }
        }
      }

    }
  }

}
