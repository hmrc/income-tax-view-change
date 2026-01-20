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

import connectors.hip.UpdateCustomerFactConnector.CorrelationIdHeader
import mocks.MockHttpV2
import models.hip.UpdateCustomerFactHipApi
import play.api.http.Status
import play.api.http.Status.{OK, UNPROCESSABLE_ENTITY}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class UpdateCustomerFactConnectorSpec extends TestSupport with MockHttpV2 {

  object TestUpdateCustomerFactConnector extends UpdateCustomerFactConnector(mockHttpClientV2, microserviceAppConfig)
  val platform: String = microserviceAppConfig.hipUrl
  val url5170 = s"$platform/etmp/RESTAdapter/customer/facts"
  val header5170: Seq[(String, String)] = microserviceAppConfig.getHIPHeaders(UpdateCustomerFactHipApi)
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

  "The UpdateCustomerFactConnector" should {
    "return a OK response" when {
      "calling updateCustomerFactsToConfirmed with a valid MTDSA ID" in {
        mockUrl5170(httpSuccessResponse)
        TestUpdateCustomerFactConnector.updateCustomerFactsToConfirmed("testMtdId").futureValue.header.status shouldBe Status.OK
      }
    }

    "return a 422 response" when {
      "calling updateCustomerFactsToConfirmed with a valid MTDSA ID return an 422 error with Errors json" in {
        mockUrl5170(http422ErrorsResponse)
        TestUpdateCustomerFactConnector.updateCustomerFactsToConfirmed("testMtdId").futureValue.header.status shouldBe Status.UNPROCESSABLE_ENTITY
      }
      "calling updateCustomerFactsToConfirmed with a valid MTDSA ID return an 422 error with Error json" in {
        mockUrl5170(http422ErrorResponse)
        TestUpdateCustomerFactConnector.updateCustomerFactsToConfirmed("testMtdId").futureValue.header.status shouldBe Status.UNPROCESSABLE_ENTITY
      }
    }
  }
}
