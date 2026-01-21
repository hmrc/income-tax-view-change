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

package connectors.hip

import mocks.MockHttpV2
import play.api.http.Status
import play.api.http.Status.{OK, UNPROCESSABLE_ENTITY}
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

import scala.concurrent.Future

class UpdateCustomerFactConnectorSpec extends TestSupport with MockHttpV2 {

  private val CorrelationIdHeader = "correlationid"

  object TestUpdateCustomerFactConnector extends UpdateCustomerFactConnector(mockHttpClientV2, microserviceAppConfig)
  val platform: String = microserviceAppConfig.hipUrl
  val url5170 = s"$platform/etmp/RESTAdapter/customer/facts"
  val successResponse: JsValue = Json.parse(
    """
      | {
      |   "processingDateTime": "2022-01-31T09:26:17Z"
      | }
      |""".stripMargin)

  val unprocessableEntityErrorResponse: JsValue = Json.parse(
    """
      | {
      |    "error":
      |    {
      |        "code": "400",
      |        "message": "Invalid request",
      |        "logID": "C0000AB8190C86300000000200006836"
      |    }
      |}
      |""".stripMargin)

  val unprocessableEntityErrorsResponse: JsValue = Json.parse(
    """
      | {
      |    "errors":
      |    {
      |        "processingDate": "2001-12-17T09:30:47Z",
      |        "code": "001",
      |        "text": "Regime missing or invalid"
      |    }
      |}
      |""".stripMargin)

  private val httpSuccessResponse = HttpResponse(OK, successResponse, Map(CorrelationIdHeader -> Seq("123")))
  private val http422ErrorsResponse = HttpResponse(UNPROCESSABLE_ENTITY, unprocessableEntityErrorsResponse, Map(CorrelationIdHeader -> Seq("123")))
  private val http422ErrorResponse = HttpResponse(UNPROCESSABLE_ENTITY, unprocessableEntityErrorResponse, Map(CorrelationIdHeader -> Seq("123")))
  lazy val mockUrl5170: HttpResponse => Unit = setupMockHttpV2PutWithHeaderCarrier(url5170)(_)
  lazy val mockUrl5170Failed: HttpResponse => Unit =
    setupMockHttpV2PutFailed(url5170)(_)

  val internalServerErrorBody: JsValue = Json.parse("""{ "error": { "code": "500", "message": "boom", "logID": "ABC" } }""")
  val serviceUnavailableBody: JsValue = Json.parse("""{ "error": { "code": "503", "message": "down", "logID": "DEF" } }""")

  private val http500 = HttpResponse(INTERNAL_SERVER_ERROR, internalServerErrorBody, Map(CorrelationIdHeader -> Seq("123")))
  private val http503 = HttpResponse(SERVICE_UNAVAILABLE, serviceUnavailableBody, Map(CorrelationIdHeader -> Seq("123")))


  "The UpdateCustomerFactConnector" should {
    "return a OK response" when {
      "calling updateCustomerFactsToConfirmed with a valid MTDSA ID" in {
        mockUrl5170(httpSuccessResponse)

        val result = TestUpdateCustomerFactConnector.updateCustomerFactsToConfirmed("testMtdId").futureValue

        result.header.status shouldBe OK
        contentAsJson(Future.successful(result)) shouldBe successResponse
      }
    }

    "return 200 and the json body even when the response doesn't match the expected schema" in {
      val unexpectedSuccessBody = Json.parse("""{ "invalidField": "value" }""")
      val httpSuccessUnexpected = HttpResponse(OK, unexpectedSuccessBody, Map(CorrelationIdHeader -> Seq("123")))

      mockUrl5170(httpSuccessUnexpected)

      val result = TestUpdateCustomerFactConnector.updateCustomerFactsToConfirmed("testMtdId").futureValue

      result.header.status shouldBe OK
      contentAsJson(Future.successful(result)) shouldBe unexpectedSuccessBody
    }


    "return a 422 response" when {
      "calling updateCustomerFactsToConfirmed with a valid MTDSA ID return an 422 error with Errors json" in {
        mockUrl5170(http422ErrorsResponse)

        val result = TestUpdateCustomerFactConnector.updateCustomerFactsToConfirmed("testMtdId").futureValue

        result.header.status shouldBe UNPROCESSABLE_ENTITY
        contentAsJson(Future.successful(result)) shouldBe unprocessableEntityErrorsResponse
      }

      "calling updateCustomerFactsToConfirmed with a valid MTDSA ID return an 422 error with Error json" in {
        mockUrl5170(http422ErrorResponse)

        val result = TestUpdateCustomerFactConnector.updateCustomerFactsToConfirmed("testMtdId").futureValue

        result.header.status shouldBe UNPROCESSABLE_ENTITY
        contentAsJson(Future.successful(result)) shouldBe unprocessableEntityErrorResponse
      }
    }

    "return 500 and pass through the response body" in {
      mockUrl5170(http500)

      val result = TestUpdateCustomerFactConnector.updateCustomerFactsToConfirmed("testMtdId").futureValue

      result.header.status shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(Future.successful(result)) shouldBe internalServerErrorBody
    }

    "return 503 and pass through the response body" in {
      mockUrl5170(http503)

      val result = TestUpdateCustomerFactConnector.updateCustomerFactsToConfirmed("testMtdId").futureValue

      result.header.status shouldBe SERVICE_UNAVAILABLE
      contentAsJson(Future.successful(result)) shouldBe serviceUnavailableBody
    }
  }
}
