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
import models.LastTaxCalculationError
import play.api.libs.json.Json
import play.mvc.Http.Status
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }
import uk.gov.hmrc.http.logging.Authorization

class FinancialDataConnectorSpec extends UnitSpec with WithFakeApplication with MockHttp {

  implicit val hc = HeaderCarrier()

  val lastTaxCalcSuccessResponse =
    HttpResponse(Status.OK, Some(Json.parse(
      """{"calcID": "testCalcId",
         "calcTimestamp": "testTimestamp",
         "calcAmount":2345.67}
      """.stripMargin.split("(?<!\\d)\\s+(?!\\d)").mkString)))

  val badJson = HttpResponse(Status.OK, responseJson = Some(Json.parse("{}")))
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, responseString = Some("Error Message"))

  object TestFinancialDataConnector extends FinancialDataConnector(mockHttpGet, fakeApplication.injector.instanceOf[MicroserviceAppConfig])

  "FinancialDataConnector.getFinancialData" should {

    import TestFinancialDataConnector._

    lazy val expectedHc =
      hc.copy(authorization =Some(Authorization(s"Bearer ${appConfig.desToken}"))).withExtraHeaders("Environment" -> appConfig.desEnvironment)

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

    "return LastTaxCalculationError model in case of bad JSON" in {
      setupMockHttpGetWithHeaderCarrier(getLastEstimatedTaxCalculationUrl(testNino, testYear, testCalcType), expectedHc)(badJson)
      val result = getLastEstimatedTaxCalculation(testNino, testYear, testCalcType)
      val enrolResponse = await(result)
      enrolResponse shouldBe LastTaxCalculationError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Financial Data ")
    }

    "return LastTaxCalculationError model in case of failed future" in {
      setupMockHttpGetFailed(getLastEstimatedTaxCalculationUrl(testNino, testYear, testCalcType))
      val result = getLastEstimatedTaxCalculation(testNino, testYear, testCalcType)
      val enrolResponse = await(result)
      enrolResponse shouldBe LastTaxCalculationError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future")
    }

  }

}
