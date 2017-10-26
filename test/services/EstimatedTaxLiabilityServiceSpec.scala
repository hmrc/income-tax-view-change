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

package services

import mocks.MockFinancialDataConnector
import models._
import play.mvc.Http.Status
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import assets.TestConstants.FinancialData._
import uk.gov.hmrc.http.HeaderCarrier

class EstimatedTaxLiabilityServiceSpec extends UnitSpec with WithFakeApplication with MockFinancialDataConnector {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  object TestEstimatedTaxLiabilityService extends EstimatedTaxLiabilityService(mockFinancialDataConnector)

  "The EstimatedTaxLiabilityService.getEstimatedTaxLiability method" when {

    "a successful response is returned from the FinancialDataConnector" should {

      "return a correctly formatted LastTaxCalculation model" in {
        setupMockFinancialDataResult(testNino, testYear, testCalcType)(expectedLastTaxCalcResponse)
        await(TestEstimatedTaxLiabilityService.getEstimatedTaxLiability(testNino, testYear, testCalcType)) shouldBe expectedLastTaxCalcResponse
      }
    }

    "an Error Response is returned from the FinancialDataConnector" should {

      "return a correctly formatted LastTaxCalculationError model" in {
        val expectedResponse = LastTaxCalculationError(Status.INTERNAL_SERVER_ERROR, "Error Message")
        setupMockFinancialDataResult(testNino, testYear, testCalcType)(lastTaxCalculationError)
        await(TestEstimatedTaxLiabilityService.getEstimatedTaxLiability(testNino, testYear, testCalcType)) shouldBe expectedResponse
      }
    }
  }
}
