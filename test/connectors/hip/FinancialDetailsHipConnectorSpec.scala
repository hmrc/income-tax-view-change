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

import connectors.httpParsers.ChargeHttpParser.{ChargeResponseError, UnexpectedChargeErrorResponse, UnexpectedChargeResponse}
import constants.FinancialDataTestConstants._
import mocks.MockHttpV2
import models.financialDetails.hip.model.{ChargesHipResponse, CodingDetailsHip}
import models.hip.{GetFinancialDetailsHipApi, GetLegacyCalcListHipApi, HipApi}
import org.mockito.stubbing.OngoingStubbing
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.{FinancialDetailsHipDataHelper, TestSupport}

import scala.concurrent.Future


class FinancialDetailsHipConnectorSpec extends TestSupport with MockHttpV2 with FinancialDetailsHipDataHelper {

  val TestFinancialDetailsConnector = new FinancialDetailsHipConnector(mockHttpClientV2, microserviceAppConfig)

  val expectedBaseUrl: String = microserviceAppConfig.hipUrl

  override implicit val hc: HeaderCarrier =
    HeaderCarrierConverter.fromRequest(FakeRequest())

  lazy val expectedUrl: String = s"$expectedBaseUrl/etmp/RESTAdapter/itsa/taxpayer/financial-details"
  lazy val queryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.getQueryStringParams(testNino, testFromDate, testToDate)

  val fullUrl: String = expectedUrl + TestFinancialDetailsConnector.buildQueryString(queryParameters)
  val fullUrlPaymentAllocationHip: String = expectedUrl + TestFinancialDetailsConnector.buildQueryString(queryParametersPaymentAllocationHip)

  val fullUrlOnlyOpenItemsHip: String = expectedUrl + TestFinancialDetailsConnector.buildQueryString(queryParametersOnlyOpenItemsTrueHip)

  def headerHip(hipApi: HipApi): Seq[(String, String)] = microserviceAppConfig.getHIPHeaders(hipApi)

  def mockHip(hipApi: HipApi): Either[ChargeResponseError, ChargesHipResponse] => OngoingStubbing[Future[Either[ChargeResponseError, ChargesHipResponse]]] =
    setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesHipResponse]](fullUrl, headerHip(hipApi))(_)

  def mockPaymentAllocationHip(hipApi: HipApi): Either[ChargeResponseError, ChargesHipResponse] => OngoingStubbing[Future[Either[ChargeResponseError, ChargesHipResponse]]] =
    setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesHipResponse]](fullUrlPaymentAllocationHip, headerHip(hipApi))(_)

  def mockOnlyOpenItemsHip(hipApi: HipApi): Either[ChargeResponseError, ChargesHipResponse] => OngoingStubbing[Future[Either[ChargeResponseError, ChargesHipResponse]]] =
    setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesHipResponse]](fullUrlOnlyOpenItemsHip, headerHip(hipApi))(_)

  "financialDetailUrl" should {
    "return the correct url" in {
      TestFinancialDetailsConnector.fullServicePath shouldBe expectedUrl
    }
  }

  "query string parameters for charge" should {
    "return the correct formatted query parameters" in {

      val actualQueryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.getQueryStringParams(
        testNino,
        testFromDate,
        testToDate
      )

      actualQueryParameters should contain theSameElementsAs expectedQueryParameters
    }
  }

  "getChargeDetails" should {
    "return a list of charges" when {
      s"$OK is received from ETMP with charges " in {
        val response = Right(testChargeHipResponse)

        mockHip(GetFinancialDetailsHipApi)(response)
        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFromDate, testToDate).futureValue

        result shouldBe response
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val response = Left(UnexpectedChargeResponse(NOT_FOUND, errorJson.toString()))
        mockHip(GetFinancialDetailsHipApi)(response)

        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFromDate, testToDate).futureValue

        result shouldBe response
      }

      "something went wrong" in {
        val response = Left(UnexpectedChargeErrorResponse)
        mockHip(GetFinancialDetailsHipApi)(response)
        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFromDate, testToDate).futureValue

        result shouldBe response
      }
    }
  }

  "paymentAllocationQuery parameters for Payment Allocation - documentId" should {
    "return the correct formatted query parameters" in {

      val actualQueryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.paymentAllocationQuery(
        testNino,
        testDocumentId
      )

      actualQueryParameters should contain theSameElementsAs expectedQueryParametersWithDocumentId
    }
  }


  "Payment Allocation - documentId" should {
    "return a list of charges" when {
      s"$OK is received from ETMP with charges " in {
        val documentDetails = List(documentDetailsHip)
        val financialDetails = List(financialDetailsHip)
        val response =
          Right(ChargesHipResponse(testTaxPayerHipDetails, testBalanceHipDetails, List(CodingDetailsHip()), documentDetails, financialDetails))

        mockPaymentAllocationHip(GetFinancialDetailsHipApi)(response)

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, testDocumentId).futureValue

        result shouldBe Right(ChargesHipResponse(testTaxPayerHipDetails, testBalanceHipDetails, List(CodingDetailsHip()), documentDetails, financialDetails))

      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val response = Left(UnexpectedChargeResponse(NOT_FOUND, errorJson.toString()))
        mockPaymentAllocationHip(GetFinancialDetailsHipApi)(response)

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, testDocumentId).futureValue

        result shouldBe response
      }

      "something went wrong" in {
        val response = Left(UnexpectedChargeErrorResponse)
        mockPaymentAllocationHip(GetFinancialDetailsHipApi)(response)

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, testDocumentId).futureValue

        result shouldBe Left(UnexpectedChargeErrorResponse)
      }

    }
  }


  "Getting only open items" should {


    "have the correct formatted query parameters" in {
      TestFinancialDetailsConnector.onlyOpenItemsQuery(testNino) should contain theSameElementsAs expectedOnlyOpenItemsQueryParameters
    }

    "return a list of charges" when {
      s"$OK is received from ETMP with charges" in {
        val expectedResponse = Right(ChargesHipResponse(
          taxpayerDetails = testTaxPayerHipDetails,
          balanceDetails = testBalanceHipDetails,
          documentDetails = List(documentDetailsHip),
          financialDetails = List(financialDetailsHip),
          codingDetails = List(CodingDetailsHip())
          )
        )

        mockOnlyOpenItemsHip(GetFinancialDetailsHipApi)(expectedResponse)
        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue
        result shouldBe expectedResponse
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val expectedErrorResponse = Left(UnexpectedChargeResponse(NOT_FOUND, errorJson.toString))
        mockOnlyOpenItemsHip(GetLegacyCalcListHipApi)(expectedErrorResponse)
        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue
        result shouldBe expectedErrorResponse
      }

      "something went wrong" in {
        val expectedErrorResponse = Left(UnexpectedChargeErrorResponse)
        mockOnlyOpenItemsHip(GetFinancialDetailsHipApi)(expectedErrorResponse)
        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue
        result shouldBe expectedErrorResponse
      }
    }
  }

}

