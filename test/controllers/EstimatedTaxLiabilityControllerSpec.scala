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

package controllers

import assets.FinancialDataTestConstants._
import assets.BaseTestConstants.testNino
import controllers.predicates.AuthenticationPredicate
import mocks.{MockAuthorisedUser, MockEstimatedTaxLiabilityService, MockUnauthorisedUser}
import play.api.http.Status
import play.api.test.FakeRequest
import uk.gov.hmrc.play.bootstrap
import play.api.test.Helpers.stubControllerComponents


class EstimatedTaxLiabilityControllerSpec extends ControllerBaseSpec with MockEstimatedTaxLiabilityService {


  "The EstimatedTaxLiabilityController.getEstimatedTaxLiability action" when {
    lazy val mockCC = stubControllerComponents()
    "called with an Authenticated user" when {

      object TestEstimatedTaxLiabilityController extends EstimatedTaxLiabilityController(
        authentication = new AuthenticationPredicate(MockAuthorisedUser, mockCC),
        estimatedTaxLiabilityService = mockEstimateTaxLiabilityService, mockCC
      )

      "a valid response from the Estimated Tax Liability Service" should {

        mockEstimateTaxLiabilityResponse(lastTaxCalc)
        lazy val result = TestEstimatedTaxLiabilityController.getEstimatedTaxLiability(testNino, testYear, testCalcType)(FakeRequest())

        checkStatusOf(result)(Status.OK)
        checkContentTypeOf(result)("application/json")
        checkJsonBodyOf(result)(lastTaxCalc)
      }

      "an invalid response from the Estimated Tax Liability Service" should {

        mockEstimateTaxLiabilityResponse(lastTaxCalculationError)
        lazy val result = TestEstimatedTaxLiabilityController.getEstimatedTaxLiability(testNino, testYear, testCalcType)(FakeRequest())

        checkStatusOf(result)(Status.INTERNAL_SERVER_ERROR)
        checkContentTypeOf(result)("application/json")
        checkJsonBodyOf(result)(lastTaxCalculationError)
      }
    }

    "called with an Unauthenticated user" should {

      object TestEstimatedTaxLiabilityController extends EstimatedTaxLiabilityController(
        authentication = new AuthenticationPredicate(MockUnauthorisedUser, mockCC),
        estimatedTaxLiabilityService = mockEstimateTaxLiabilityService, mockCC
      )

      lazy val result = TestEstimatedTaxLiabilityController.getEstimatedTaxLiability(testNino, testYear, testCalcType)(FakeRequest())

      checkStatusOf(result)(Status.UNAUTHORIZED)
    }
  }
}
