/*
 * Copyright 2020 HM Revenue & Customs
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

import assets.FinancialDataTestConstants
import assets.FinancialDataTestConstants.{charges1, charges2}
import connectors.httpParsers.ChargeHttpParser.{ChargeResponseError, UnexpectedChargeResponse}
import mocks.MockHttp
import models.financialDetails.Charge
import models.financialDetails.responses.ChargesResponse
import play.api.http.Status.OK
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestSupport

class FinancialDetailsConnectorSpec extends TestSupport with MockHttp {

  object TestFinancialDetailsConnector extends FinancialDetailsConnector(mockHttpGet, microserviceAppConfig)

  val testNino: String = "testNino"
  val testFrom: String = "testFrom"
  val testTo: String = "testTo"

  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "financialDetailUrl" should {
    "return the correct url" in {
      val expectedUrl: String = s"${microserviceAppConfig.desUrl}/enterprise/02.00.00/financial-data/NINO/$testNino/ITSA"
      val actualUrl: String = TestFinancialDetailsConnector.financialDetailsUrl(testNino)

      actualUrl shouldBe expectedUrl
    }
  }

  "queryParameters for charge" should {
    "return the correct formatted query parameters" in {
      val expectedQueryParameters: Seq[(String, String)] = Seq(
        "dateFrom" -> testFrom,
        "dateTo" -> testTo
      )
      val actualQueryParameters: Seq[(String, String)] = TestFinancialDetailsConnector.queryParameters(
        from = testFrom,
        to = testTo
      )

      actualQueryParameters shouldBe expectedQueryParameters
    }
  }

  "listCharges" should {
    "return a list of charges" when {
      s"$OK is received from ETMP with charges " in {

        mockDesGet(
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.queryParameters(testFrom, testTo),
          headerCarrier = TestFinancialDetailsConnector.desHeaderCarrier
        )(Right(ChargesResponse(List(charges1, charges2))))

        val result = await(TestFinancialDetailsConnector.listCharges(testNino, testFrom, testTo))

        result shouldBe Right(ChargesResponse(List(charges1, charges2)))
      }
    }

    "return OK without a list of charges" when {
      s"$OK is received from ETMP with no charges" in {
        mockDesGet(
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.queryParameters(testFrom, testTo),
          headerCarrier = TestFinancialDetailsConnector.desHeaderCarrier
        )(Right(FinancialDataTestConstants.testEmptyChargeHttpResponse))

        val result = await(TestFinancialDetailsConnector.listCharges(testNino, testFrom, testTo))

        result shouldBe Right(FinancialDataTestConstants.testEmptyChargeHttpResponse)

      }
    }

    s"return an error" when {
      "something went wrong" in {
        mockDesGet[ChargeResponseError, Charge](
          url = TestFinancialDetailsConnector.financialDetailsUrl(testNino),
          queryParameters = TestFinancialDetailsConnector.queryParameters(testFrom, testTo),
          headerCarrier = TestFinancialDetailsConnector.desHeaderCarrier
        )(Left(UnexpectedChargeResponse))

        val result = await(TestFinancialDetailsConnector.listCharges(testNino, testFrom, testTo))

        result shouldBe Left(UnexpectedChargeResponse)
      }
    }
  }
}

