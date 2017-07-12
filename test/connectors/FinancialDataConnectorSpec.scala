/*
 * Copyright 2017 HM Revenue & Customs
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

import assets.TestConstants.FinancialData._
import config.MicroserviceAppConfig
import mocks.MockHttp
import play.api.libs.json.Json
import play.mvc.Http.Status
import uk.gov.hmrc.play.http.{HeaderCarrier, HeaderNames, HttpResponse}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class FinancialDataConnectorSpec extends UnitSpec with WithFakeApplication with MockHttp {

  implicit val hc = HeaderCarrier()

  val lastTaxCalcSuccessResponse =
    HttpResponse(Status.OK, Some(Json.parse(
      """{"calcID": "testCalcId",
         "calcTimestamp": "testTimestamp",
         "calcAmount":2345.67}
      """.stripMargin.split("(?<!\\d)\\s+(?!\\d)").mkString)))

  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, responseString = Some("Error Message"))

  object TestFinancialDataConnector extends FinancialDataConnector(mockHttpGet, fakeApplication.injector.instanceOf[MicroserviceAppConfig])

  "FinancialDataConnector.getFinancialData" should {

    import TestFinancialDataConnector._

    lazy val expectedHc = hc.withExtraHeaders(
      HeaderNames.authorisation -> appConfig.desToken,
      "Environment" -> appConfig.desEnvironment
    )

    "return Status (OK) and a JSON body when successful as a LatTaxCalculation model" in {
      setupMockHttpGetWithHeaderCarrier(getLastEstimatedTaxCalculationUrl(testNino, testYear, testCalcType), expectedHc)(lastTaxCalcSuccessResponse)
      val result = getLastEstimatedTaxCalculation(testNino, testYear, testCalcType)
      val enrolResponse = await(result)
      enrolResponse shouldBe expectedLastTaxCalcResponse
    }

    "return LastTaxCalculationError model in case of failure" in {
      setupMockHttpGetWithHeaderCarrier(getLastEstimatedTaxCalculationUrl(testNino, testYear, testCalcType), expectedHc)(badResponse)
      val result = getLastEstimatedTaxCalculation(testNino, testYear, testCalcType)
      val enrolResponse = await(result)
      enrolResponse shouldBe lastTaxCalculationError
    }

  }

}
