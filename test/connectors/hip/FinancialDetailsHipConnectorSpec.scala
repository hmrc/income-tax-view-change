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
import models.financialDetails.responses.ChargesResponse
import models.hip.{GetFinancialDetailsHipApi, HipApi}
import org.mockito.stubbing.OngoingStubbing
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.TestSupport

import scala.concurrent.Future


class FinancialDetailsHipConnectorSpec extends TestSupport with MockHttpV2 {

  val TestFinancialDetailsConnector = new FinancialDetailsHipConnector(mockHttpClientV2, microserviceAppConfig)

  val expectedBaseUrl: String = microserviceAppConfig.hipUrl

  val expectedApiHeaders: Seq[(String, String)] = Seq(
    "Environment" -> "localIFEnvironment",
    "Authorization" -> "Bearer localIFToken1553"
  )

  override implicit val hc: HeaderCarrier =
    HeaderCarrierConverter.fromRequest(FakeRequest())

  val testNino: String = "AA123456A"
  val testFrom: String = "2021-12-12"
  val testTo: String = "2022-12-12"
  val documentId: String = "123456789"

  val queryParametersOnlyOpenItemsTrue: Seq[(String, String)] = Seq(
    "onlyOpenItems" -> "true",
    "includeLocks" -> "true",
    "calculateAccruedInterest" -> "true",
    "removePOA" -> "false",
    "customerPaymentInformation" -> "true",
    "includeStatistical" -> "false"
  )

  val queryParametersOnlyOpenItemsTrueHip: Seq[(String, String)] = Seq(
    "calculateAccruedInterest" -> "true",
    "customerPaymentInformation" -> "true",
    "idNumber" -> "AA123456A",
    "idType" -> "NINO",
    "includeLocks" -> "true",
    "includeStatistical" -> "false",
    "onlyOpenItems" -> "true",
    "regimeType" -> "ITSA",
    "removePaymentonAccount" -> "false"
  )

  val queryParametersPaymentAllocationHip: Seq[(String, String)] = Seq(
    "sapDocumentNumber" -> documentId,
    "calculateAccruedInterest" -> "true",
    "customerPaymentInformation" -> "true",
    "idNumber" -> "AA123456A",
    "idType" -> "NINO",
    "includeLocks" -> "true",
    "includeStatistical" -> "false",
    "onlyOpenItems" -> "false",
    "regimeType" -> "ITSA",
    "removePaymentonAccount" -> "false"
  )

  val queryParametersPaymentAllocation: Seq[(String, String)] = Seq(
    "docNumber" -> documentId,
    "onlyOpenItems" -> "false",
    "includeLocks" -> "true",
    "calculateAccruedInterest" -> "true",
    "removePOA" -> "false",
    "customerPaymentInformation" -> "true",
    "includeStatistical" -> "false"
  )

  lazy val expectedUrl: String = s"$expectedBaseUrl/RESTAdapter/itsa/taxpayer/financial-details"
  lazy val queryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.getQueryStringParams(testNino, testFrom, testTo)
  val fullUrl: String = expectedUrl + TestFinancialDetailsConnector.buildQueryString(queryParameters)
  val fullUrlPaymentAllocation: String = expectedUrl + TestFinancialDetailsConnector.buildQueryString(queryParametersPaymentAllocation)
  val fullUrlPaymentAllocationHip: String = expectedUrl + TestFinancialDetailsConnector.buildQueryString(queryParametersPaymentAllocationHip)

  val fullUrlOnlyOpenItems: String = expectedUrl + TestFinancialDetailsConnector.buildQueryString(queryParametersOnlyOpenItemsTrue)
  val fullUrlOnlyOpenItemsHip: String = expectedUrl + TestFinancialDetailsConnector.buildQueryString(queryParametersOnlyOpenItemsTrueHip)

  val header: Seq[(String, String)] = microserviceAppConfig.getIFHeaders("1553")

  def headerHip(hipApi: HipApi): Seq[(String, String)] = microserviceAppConfig.getHIPHeaders(hipApi)

  val mock = setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesResponse]](fullUrl, header)(_)

  def mockHip(hipApi: HipApi): Either[ChargeResponseError, ChargesHipResponse] => OngoingStubbing[Future[Either[ChargeResponseError, ChargesHipResponse]]] =
    setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesHipResponse]](fullUrl, headerHip(hipApi))(_)

  val mockPaymentAllocation = setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesResponse]](fullUrlPaymentAllocation, header)(_)

  def mockPaymentAllocationHip(hipApi: HipApi): Either[ChargeResponseError, ChargesHipResponse] => OngoingStubbing[Future[Either[ChargeResponseError, ChargesHipResponse]]] =
    setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesHipResponse]](fullUrlPaymentAllocationHip, headerHip(hipApi))(_)

  val mockOnlyOpenItems = setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesResponse]](fullUrlOnlyOpenItems, header)(_)
  def mockOnlyOpenItemsHip(hipApi: HipApi): Either[ChargeResponseError, ChargesHipResponse] => OngoingStubbing[Future[Either[ChargeResponseError, ChargesHipResponse]]] =
    setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesHipResponse]](fullUrlOnlyOpenItemsHip, headerHip(hipApi))(_)


  "financialDetailUrl" should {
    "return the correct url" in {
      TestFinancialDetailsConnector.fullServicePath shouldBe expectedUrl
    }
  }

  "query string parameters for charge" should {
    "return the correct formatted query parameters" in {
      val expectedQueryParameters: Seq[(String, String)] = Seq(
        "dateFrom" -> testFrom,
        "dateTo" -> testTo,
        "onlyOpenItems" -> "false",
        "includeLocks" -> "true",
        "calculateAccruedInterest" -> "true",
        "removePaymentonAccount" -> "false",
        "customerPaymentInformation" -> "true",
        "includeStatistical" -> "false",
        "idNumber" -> "AA123456A",
        "idType" -> "NINO",
        "regimeType" -> "ITSA"
      )
      val actualQueryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.getQueryStringParams(
        testNino,
        testFrom,
        testTo
      )

      actualQueryParameters should contain theSameElementsAs expectedQueryParameters
    }
  }

  "getChargeDetails" should {
    "return a list of charges" when {
      s"$OK is received from ETMP with charges " in {
        val response = Right(testChargeHipResponse)

        mockHip(GetFinancialDetailsHipApi)(response)
        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo).futureValue

        result shouldBe response
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val response = Left(UnexpectedChargeResponse(404, errorJson.toString()))
        mockHip(GetFinancialDetailsHipApi)(response)

        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo).futureValue

        result shouldBe response
      }

      "something went wrong" in {
        val response = Left(UnexpectedChargeErrorResponse)
        mockHip(GetFinancialDetailsHipApi)(response)
        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo).futureValue

        result shouldBe response
      }
    }
  }

  "paymentAllocationQuery parameters for Payment Allocation - documentId" should {
    "return the correct formatted query parameters" in {
      val expectedQueryParameters: Seq[(String, String)] = Seq(
        "sapDocumentNumber" -> documentId,
        "onlyOpenItems" -> "false",
        "includeLocks" -> "true",
        "calculateAccruedInterest" -> "true",
        "removePaymentonAccount" -> "false",
        "customerPaymentInformation" -> "true",
        "includeStatistical" -> "false",
        "idNumber" -> "AA123456A",
        "idType" -> "NINO",
        "regimeType" -> "ITSA"
      )
      val actualQueryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.paymentAllocationQuery(
        testNino,
        documentId
      )

      actualQueryParameters should contain theSameElementsAs expectedQueryParameters
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

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId).futureValue

        result shouldBe Right(ChargesHipResponse(testTaxPayerHipDetails, testBalanceHipDetails, List(CodingDetailsHip()), documentDetails, financialDetails))

      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val response = Left(UnexpectedChargeResponse(404, errorJson.toString()))
        mockPaymentAllocationHip(GetFinancialDetailsHipApi)(response)

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId).futureValue

        result shouldBe response
      }

      "something went wrong" in {
        val response = Left(UnexpectedChargeErrorResponse)
        mockPaymentAllocationHip(GetFinancialDetailsHipApi)(response)

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId).futureValue

        result shouldBe Left(UnexpectedChargeErrorResponse)
      }

    }
  }


  "Getting only open items" should {

    val expectedOnlyOpenItemsQueryParameters: Seq[(String, String)] = Seq(
      //"sapDocumentNumber" -> documentId,
      "calculateAccruedInterest" -> "true",
      "customerPaymentInformation" -> "true",
      "idNumber" -> "AA123456A",
      "idType" -> "NINO",
      "includeLocks" -> "true",
      "includeStatistical" -> "false",
      "onlyOpenItems" -> "true",
      "regimeType" -> "ITSA",
      "removePaymentonAccount" -> "false"
    )

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

//    s"return an error" when {
//      "when no data found is returned" in {
//        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
//        val expectedErrorResponse = Left(UnexpectedChargeResponse(NOT_FOUND, errorJson.toString))
//
//        mockOnlyOpenItems(expectedErrorResponse)
//
//        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue
//
//        result shouldBe expectedErrorResponse
//      }
//
//      "something went wrong" in {
//        val expectedErrorResponse = Left(UnexpectedChargeErrorResponse)
//
//        mockOnlyOpenItems(expectedErrorResponse)
//
//        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue
//
//        result shouldBe expectedErrorResponse
//      }
//    }
  }

}

