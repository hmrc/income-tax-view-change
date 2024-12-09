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

package connectors

import assets.FinancialDataTestConstants._
import connectors.httpParsers.ChargeHttpParser.{ChargeResponseError, UnexpectedChargeErrorResponse, UnexpectedChargeResponse}
import mocks.MockHttpV2
import models.financialDetails.responses.ChargesResponse
import models.financialDetails.{DocumentDetail, FinancialDetail}
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.TestSupport


class FinancialDetailsConnectorSpec extends TestSupport with MockHttpV2 {

  val TestFinancialDetailsConnector = new FinancialDetailsConnector(mockHttpClientV2, microserviceAppConfig)

  val expectedBaseUrl: String = microserviceAppConfig.ifUrl

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

  val queryParametersPaymentAllocation: Seq[(String, String)] = Seq(
    "docNumber" -> documentId,
    "onlyOpenItems" -> "false",
    "includeLocks" -> "true",
    "calculateAccruedInterest" -> "true",
    "removePOA" -> "false",
    "customerPaymentInformation" -> "true",
    "includeStatistical" -> "false"
  )

  lazy val expectedUrl: String = s"$expectedBaseUrl/enterprise/02.00.00/financial-data/NINO/$testNino/ITSA"
  lazy val queryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.chargeDetailsQuery(testFrom, testTo)
  val fullUrl: String = expectedUrl + TestFinancialDetailsConnector.makeQueryString(queryParameters)
  val fullUrlPaymentAllocation: String = expectedUrl + TestFinancialDetailsConnector.makeQueryString(queryParametersPaymentAllocation)
  val fullUrlOnlyOpenItems: String = expectedUrl + TestFinancialDetailsConnector.makeQueryString(queryParametersOnlyOpenItemsTrue)
  val header: Seq[(String, String)] = microserviceAppConfig.getIFHeaders("1553")

  val mock = setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesResponse]](fullUrl, header)(_)
  val mockPaymentAllocation = setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesResponse]](fullUrlPaymentAllocation, header)(_)
  val mockOnlyOpenItems = setupMockHttpGetWithHeaderCarrier[Either[ChargeResponseError, ChargesResponse]](fullUrlOnlyOpenItems, header)(_)

  "financialDetailUrl" should {
    "return the correct url" in {
      TestFinancialDetailsConnector.financialDetailsUrl(testNino) shouldBe expectedUrl
    }
  }

  "chargeDetailsQuery parameters for charge" should {
    "return the correct formatted query parameters" in {
      val expectedQueryParameters: Seq[(String, String)] = Seq(
        "dateFrom" -> testFrom,
        "dateTo" -> testTo,
        "onlyOpenItems" -> "false",
        "includeLocks" -> "true",
        "calculateAccruedInterest" -> "true",
        "removePOA" -> "false",
        "customerPaymentInformation" -> "true",
        "includeStatistical" -> "false"
      )
      val actualQueryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.chargeDetailsQuery(
        from = testFrom,
        to = testTo
      )

      actualQueryParameters shouldBe expectedQueryParameters
    }
  }

  "getChargeDetails" should {
    "return a list of charges" when {
      s"$OK is received from ETMP with charges " in {
        val response = Right(testChargesResponse)

        mock(response)
        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo).futureValue

        result shouldBe response
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val response = Left(UnexpectedChargeResponse(404, errorJson.toString()))
        mock(response)

        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo).futureValue

        result shouldBe response
      }

      "something went wrong" in {
        val response = Left(UnexpectedChargeErrorResponse)
        mock(response)
        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo).futureValue

        result shouldBe response
      }
    }
  }

  "paymentAllocationQuery parameters for Payment Allocation - documentId" should {
    "return the correct formatted query parameters" in {
      val expectedQueryParameters: Seq[(String, String)] = Seq(
        "docNumber" -> documentId,
        "onlyOpenItems" -> "false",
        "includeLocks" -> "true",
        "calculateAccruedInterest" -> "true",
        "removePOA" -> "false",
        "customerPaymentInformation" -> "true",
        "includeStatistical" -> "false"
      )
      val actualQueryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.paymentAllocationQuery(
        documentId = documentId
      )

      actualQueryParameters shouldBe expectedQueryParameters
    }
  }

  "Payment Allocation - documentId" should {
    "return a list of charges" when {
      s"$OK is received from ETMP with charges " in {
        val documentDetails: List[DocumentDetail] = List(documentDetail)
        val financialDetails: List[FinancialDetail] = List(financialDetail)
        val response = Right(ChargesResponse(testBalanceDetails, documentDetails, financialDetails))
        mockPaymentAllocation(response)
        println(mock(response))

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId).futureValue

        result shouldBe Right(ChargesResponse(testBalanceDetails, documentDetails, financialDetails))
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val response = Left(UnexpectedChargeResponse(404, errorJson.toString()))
        mockPaymentAllocation(response)

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId).futureValue

        result shouldBe response
      }

      "something went wrong" in {
        val response = Left(UnexpectedChargeErrorResponse)
        mockPaymentAllocation(response)

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId).futureValue

        result shouldBe Left(UnexpectedChargeErrorResponse)
      }
    }
  }

  "Getting only open items" should {

    val expectedOnlyOpenItemsQueryParameters: Seq[(String, String)] = Seq(
      "onlyOpenItems" -> "true",
      "includeLocks" -> "true",
      "calculateAccruedInterest" -> "true",
      "removePOA" -> "false",
      "customerPaymentInformation" -> "true",
      "includeStatistical" -> "false"
    )

    "have the correct formatted query parameters" in {
      TestFinancialDetailsConnector.onlyOpenItemsQuery() shouldBe expectedOnlyOpenItemsQueryParameters
    }

    "return a list of charges" when {
      s"$OK is received from ETMP with charges" in {
        val expectedResponse = Right(ChargesResponse(
          balanceDetails = testBalanceDetails,
          documentDetails = List(documentDetail),
          financialDetails = List(financialDetail)))

        mockOnlyOpenItems(expectedResponse)

        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue

        result shouldBe expectedResponse
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val expectedErrorResponse = Left(UnexpectedChargeResponse(NOT_FOUND, errorJson.toString))

        mockOnlyOpenItems(expectedErrorResponse)

        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue

        result shouldBe expectedErrorResponse
      }

      "something went wrong" in {
        val expectedErrorResponse = Left(UnexpectedChargeErrorResponse)

        mockOnlyOpenItems(expectedErrorResponse)

        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue

        result shouldBe expectedErrorResponse
      }
    }
  }
}

