/*
 * Copyright 2018 HM Revenue & Customs
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

import assets.TestConstants.FinancialData._
import assets.TestConstants._
import mocks.MockFinancialDataConnector
import models._
import utils.TestSupport

import scala.concurrent.Future

class EstimatedTaxLiabilityServiceSpec extends TestSupport with MockFinancialDataConnector {

  object TestEstimatedTaxLiabilityService extends EstimatedTaxLiabilityService(mockFinancialDataConnector)
  def result: Future[LastTaxCalculationResponseModel] = TestEstimatedTaxLiabilityService.getEstimatedTaxLiability(testNino, testYear, testCalcType)

  "The EstimatedTaxLiabilityService.getEstimatedTaxLiability method" when {

    "a successful response is returned from the FinancialDataConnector" should {

      "return a correctly formatted LastTaxCalculation model" in {
        mockFinancialDataResult(lastTaxCalc)
        await(result) shouldBe lastTaxCalc
      }
    }

    "an Error Response is returned from the FinancialDataConnector" should {

      "return a correctly formatted LastTaxCalculationError model" in {
        mockFinancialDataResult(lastTaxCalculationError)
        await(result) shouldBe lastTaxCalculationError
      }
    }
  }
}
