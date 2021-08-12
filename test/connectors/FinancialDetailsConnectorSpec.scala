/*
 * Copyright 2021 HM Revenue & Customs
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

import assets.FinancialDataTestConstants.{documentDetail, financialDetail, testChargesResponse}
import connectors.httpParsers.ChargeHttpParser.{ChargeResponseError, UnexpectedChargeErrorResponse, UnexpectedChargeResponse}
import mocks.MockHttp
import models.financialDetails.responses.ChargesResponse
import models.financialDetails.{DocumentDetail, FinancialDetail}
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.TestSupport

import java.util.UUID

class FinancialDetailsConnectorDESSpec extends FinancialDetailsConnectorBehavior[FinancialDetailsConnectorDES] {
  override val TestFinancialDetailsConnector = new FinancialDetailsConnectorDES(mockHttpGet, microserviceAppConfig)

  override val expectedBaseUrl: String = microserviceAppConfig.desUrl

  override val expectedHeaders = Seq(
    "Environment"   -> "localDESEnvironment",
    "Authorization" -> "Bearer localDESToken"
  )
}

class FinancialDetailsConnectorIFSpec extends FinancialDetailsConnectorBehavior[FinancialDetailsConnectorIF] {
  override val TestFinancialDetailsConnector = new FinancialDetailsConnectorIF(mockHttpGet, microserviceAppConfig)

  override val expectedBaseUrl: String = microserviceAppConfig.ifUrl

  private val requestId = "mdtp-request-id"

  override val expectedHeaders = Seq(
    "Environment"   -> "localIFEnvironment",
    "Authorization" -> "Bearer localIFToken",
    "CorrelationId" -> requestId
  )

  override implicit val hc: HeaderCarrier =
    HeaderCarrierConverter.fromRequest(FakeRequest().withHeaders(HeaderNames.xRequestId -> requestId))

  "FinancialDetailsConnectorIF" should {
    "generate a random UUID for CorrelationId header when there is no X-Request-ID header available in request" in {
      def correlationIdInHeaders(): Option[UUID] = TestFinancialDetailsConnector.headers(hc = HeaderCarrier())
          .collectFirst { case ("CorrelationId", value) => UUID.fromString(value) }

      val id1 = correlationIdInHeaders()
      val id2 = correlationIdInHeaders()

      id1 shouldBe defined
      id2 shouldBe defined
      id1 should not be id2
    }
  }
}

abstract class FinancialDetailsConnectorBehavior[C <: FinancialDetailsConnector] extends TestSupport with MockHttp {

  def TestFinancialDetailsConnector: C
  def expectedBaseUrl: String
  def expectedHeaders: Seq[(String, String)]

  val testNino: String = "testNino"
  val testFrom: String = "testFrom"
  val testTo: String = "testTo"
  val documentId: String = "123456789"

  "financialDetailUrl" should {
    "return the correct url" in {
      val expectedUrl: String = s"$expectedBaseUrl/enterprise/02.00.00/financial-data/NINO/$testNino/ITSA"
      val actualUrl: String = TestFinancialDetailsConnector.financialDetailsUrl(testNino)

      actualUrl shouldBe expectedUrl
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
          headers = expectedHeaders
        )(Right(testChargesResponse))

        val result = await(TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo))

        result shouldBe Right(testChargesResponse)
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.chargeDetailsQuery(testFrom, testTo),
          headers = expectedHeaders
        )(Left(UnexpectedChargeResponse(404, errorJson.toString())))

        val result = await(TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo))

        result shouldBe Left(UnexpectedChargeResponse(404, errorJson.toString()))
      }
      "something went wrong" in {
        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.chargeDetailsQuery(testFrom, testTo),
          headers = expectedHeaders
        )(Left(UnexpectedChargeErrorResponse))

        val result = await(TestFinancialDetailsConnector.getChargeDetails(testNino, testFrom, testTo))

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
          url = TestFinancialDetailsConnector.paymentAllocationFinancialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.paymentAllocationQuery(documentId),
          headers = expectedHeaders
        )(Right(ChargesResponse(documentDetails, financialDetails)))

        val result = await(TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId))

        result shouldBe Right(ChargesResponse(documentDetails, financialDetails))
      }
    }

    s"return an error" when {
      "when no data found is returned" in {
        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.paymentAllocationFinancialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.paymentAllocationQuery(documentId),
          headers = expectedHeaders
        )(Left(UnexpectedChargeResponse(404, errorJson.toString())))

        val result = await(TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId))

        result shouldBe Left(UnexpectedChargeResponse(404, errorJson.toString()))
      }
      "something went wrong" in {
        mockDesGet[ChargeResponseError, ChargesResponse](
          url = TestFinancialDetailsConnector.paymentAllocationFinancialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.paymentAllocationQuery(documentId),
          headers = expectedHeaders
        )(Left(UnexpectedChargeErrorResponse))

        val result = await(TestFinancialDetailsConnector.getPaymentAllocationDetails(testNino, documentId))

        result shouldBe Left(UnexpectedChargeErrorResponse)
      }
    }
  }
}

