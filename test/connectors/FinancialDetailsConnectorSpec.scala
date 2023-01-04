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
import mocks.MockHttp
import models.financialDetails.responses.ChargesResponse
import models.financialDetails.{DocumentDetail, FinancialDetail}
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.TestSupport

class FinancialDetailsConnectorDESSpec extends FinancialDetailsConnectorBehavior[FinancialDetailsConnectorDES] {
  override val TestFinancialDetailsConnector = new FinancialDetailsConnectorDES(mockHttpGet, microserviceAppConfig)

  override val expectedBaseUrl: String = microserviceAppConfig.desUrl

  override val expectedApiHeaders = Seq(
    "Environment" -> "localDESEnvironment",
    "Authorization" -> "Bearer localDESToken"
  )
}

class FinancialDetailsConnectorIFSpec extends FinancialDetailsConnectorBehavior[FinancialDetailsConnectorIF] {
  override val TestFinancialDetailsConnector = new FinancialDetailsConnectorIF(mockHttpGet, microserviceAppConfig)

  override val expectedBaseUrl: String = microserviceAppConfig.ifUrl

  override val expectedApiHeaders = Seq(
    "Environment" -> "localIFEnvironment",
    "Authorization" -> "Bearer localIFToken"
  )

  override implicit val hc: HeaderCarrier =
    HeaderCarrierConverter.fromRequest(FakeRequest())
}

abstract class FinancialDetailsConnectorBehavior[C <: FinancialDetailsConnector] extends TestSupport with MockHttp {

  def TestFinancialDetailsConnector: C

  def expectedBaseUrl: String

  def expectedApiHeaders: Seq[(String, String)]

  val testNino: String = "testNino"
  val testFrom: String = "testFrom"
  val testTo: String = "testTo"
  val documentId: String = "123456789"

  lazy val expectedUrl: String = s"$expectedBaseUrl/enterprise/02.00.00/financial-data/NINO/$testNino/ITSA"

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
        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.chargeDetailsQuery(testFrom, testTo),
          headers = expectedApiHeaders
        )(Right(testChargesResponse))

        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo).futureValue

        result shouldBe Right(testChargesResponse)
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.chargeDetailsQuery(testFrom, testTo),
          headers = expectedApiHeaders
        )(Left(UnexpectedChargeResponse(404, errorJson.toString())))

        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo).futureValue

        result shouldBe Left(UnexpectedChargeResponse(404, errorJson.toString()))
      }
      "something went wrong" in {
        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.chargeDetailsQuery(testFrom, testTo),
          headers = expectedApiHeaders
        )(Left(UnexpectedChargeErrorResponse))

        val result = TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo).futureValue

        result shouldBe Left(UnexpectedChargeErrorResponse)
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

        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.paymentAllocationQuery(documentId),
          headers = expectedApiHeaders
        )(Right(ChargesResponse(testBalanceDetails, documentDetails, financialDetails)))

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId).futureValue

        result shouldBe Right(ChargesResponse(testBalanceDetails, documentDetails, financialDetails))
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.paymentAllocationQuery(documentId),
          headers = expectedApiHeaders
        )(Left(UnexpectedChargeResponse(404, errorJson.toString())))

        val result = TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId).futureValue

        result shouldBe Left(UnexpectedChargeResponse(404, errorJson.toString()))
      }
      "something went wrong" in {
        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.paymentAllocationQuery(documentId),
          headers = expectedApiHeaders
        )(Left(UnexpectedChargeErrorResponse))

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

        mockDesGet[ChargeResponseError, ChargesResponse](
          url = expectedUrl,
          queryParameters = expectedOnlyOpenItemsQueryParameters,
          headers = expectedApiHeaders
        )(expectedResponse)

        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue

        result shouldBe expectedResponse
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        val expectedErrorResponse = Left(UnexpectedChargeResponse(NOT_FOUND, errorJson.toString))

        mockDesGet[ChargeResponseError, ChargesResponse](
          url = expectedUrl,
          queryParameters = expectedOnlyOpenItemsQueryParameters,
          headers = expectedApiHeaders
        )(expectedErrorResponse)

        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue

        result shouldBe expectedErrorResponse
      }

      "something went wrong" in {
        val expectedErrorResponse = Left(UnexpectedChargeErrorResponse)

        mockDesGet[ChargeResponseError, ChargesResponse](
          url = expectedUrl,
          queryParameters = expectedOnlyOpenItemsQueryParameters,
          headers = expectedApiHeaders
        )(expectedErrorResponse)

        val result = TestFinancialDetailsConnector.getOnlyOpenItems(testNino).futureValue

        result shouldBe expectedErrorResponse
      }
    }
  }
}

