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

package controllers

import assets.TestConstants.FinancialData._
import auth.{MockAuthorisedUser, MockUnauthorisedUser}
import config.MicroserviceAppConfig
import controllers.predicates.AuthenticationPredicate
import mocks.MockEstimatedTaxLiabilityService
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import utils.TestSupport

import scala.concurrent.Future


class EstimatedTaxLiabilityControllerSpec extends ControllerBaseSpec with MockEstimatedTaxLiabilityService {

  "The EstimatedTaxLiabilityController.getEstimatedTaxLiability action" when {

    "called with an Authenticated user" when {

      object TestEstimatedTaxLiabilityController extends EstimatedTaxLiabilityController()(
        appConfig = app.injector.instanceOf[MicroserviceAppConfig],
        authentication = new AuthenticationPredicate(MockAuthorisedUser),
        estimatedTaxLiabilityService = mockEstimateTaxLiabilityService
      )

      "a valid response from the Estimated Tax Liability Service" should {

        def result: Future[Result] = {
          mockEstimateTaxLiabilityResponse(lastTaxCalc)
          TestEstimatedTaxLiabilityController.getEstimatedTaxLiability(testNino, testYear, testCalcType)(FakeRequest())
        }

        checkStatusOf(result)(Status.OK)
        checkContentTypeOf(result)("application/json")
        checkBodyOf(result)(lastTaxCalc)
      }

      "an invalid response from the Estimated Tax Liability Service" should {

        def result: Future[Result] = {
          mockEstimateTaxLiabilityResponse(lastTaxCalculationError)
          TestEstimatedTaxLiabilityController.getEstimatedTaxLiability(testNino, testYear, testCalcType)(FakeRequest())
        }

        checkStatusOf(result)(Status.INTERNAL_SERVER_ERROR)
        checkContentTypeOf(result)("application/json")
        checkBodyOf(result)(lastTaxCalculationError)
      }
    }

    "called with an Unauthenticated user" should {

      object TestEstimatedTaxLiabilityController extends EstimatedTaxLiabilityController()(
        appConfig = app.injector.instanceOf[MicroserviceAppConfig],
        authentication = new AuthenticationPredicate(MockUnauthorisedUser),
        estimatedTaxLiabilityService = mockEstimateTaxLiabilityService
      )

      lazy val result = TestEstimatedTaxLiabilityController.getEstimatedTaxLiability(testNino, testYear, testCalcType)(FakeRequest())

      checkStatusOf(result)(Status.UNAUTHORIZED)
    }
  }
}
