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

package controllers

import constants.BaseTestConstants.{testNino, testTaxYearEnd, testTaxYearRange}
import constants.CalculationListTestConstants._
import controllers.predicates.AuthenticationPredicate
import mocks.{MockCalculationListService, MockMicroserviceAuthConnector}
import models.calculationList.CalculationListResponseModel
import models.errors
import models.errors.{InvalidNino, InvalidTaxYear}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status, stubControllerComponents}
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class CalculationListControllerSpec extends ControllerBaseSpec with MockMicroserviceAuthConnector with MockCalculationListService {
  val mockCC: ControllerComponents = stubControllerComponents()
  val authPredicate = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig)
  val successResponse: Either[Nothing, CalculationListResponseModel] = Right(calculationListFull)

  object TestCalculationListController extends CalculationListController(authPredicate, mockCalculationListService, mockCC)

  "CalculationListController.getCalculationList" should {
    "return 200 OK" when {
      "user is authenticated and CalculationListService returns a success response" in {
        mockAuth()
        setupMockGetCalculationList(testNino, testTaxYearEnd)(successResponse)

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(calculationListFull.calculations.head)
      }
    }
    "return 400 BAD_REQUEST" when {
      "CalculationListService returns a single 400 BAD_REQUEST error" in {
        mockAuth()
        setupMockGetCalculationList(testNino, testTaxYearEnd)(badRequestSingleError)

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(singleError)
      }
      "CalculationListService returns multiple errors" in {
        mockAuth()
        setupMockGetCalculationList(testNino, testTaxYearEnd)(badRequestMultiError)

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(multiError)
      }
      "NINO is invalid" in {
        val invalidNino = "GB123456E"
        mockAuth()
        setupMockGetCalculationList(invalidNino, testTaxYearEnd)(badRequestSingleError)

        val result = TestCalculationListController.getCalculationList(invalidNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson[errors.Error](InvalidNino)
      }
      "tax year is invalid" in {
        val invalidTaxYear = "3000"
        mockAuth()
        setupMockGetCalculationList(testNino, invalidTaxYear)(badRequestSingleError)

        val result = TestCalculationListController.getCalculationListTYS(testNino, invalidTaxYear)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson[errors.Error](InvalidTaxYear)
      }
    }
    "return 401 UNAUTHORIZED" when {
      "called with an unauthenticated user" in {
        mockAuth(Future.failed(new MissingBearerToken))

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }
  }

  "CalculationListController.getCalculationListTYS" should {
    "return 200 OK" when {
      "user is authenticated and CalculationListService returns a success response" in {
        mockAuth()
        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(successResponse)

        val result = TestCalculationListController.getCalculationListTYS(testNino, testTaxYearRange)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(calculationListFull.calculations.head)
      }
    }
    "return 400 BAD_REQUEST" when {
      "CalculationListService returns a single 400 BAD_REQUEST error" in {
        mockAuth()
        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(badRequestSingleError)

        val result = TestCalculationListController.getCalculationListTYS(testNino, testTaxYearRange)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(singleError)
      }
      "CalculationListService returns multiple errors" in {
        mockAuth()
        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(badRequestMultiError)

        val result = TestCalculationListController.getCalculationListTYS(testNino, testTaxYearRange)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(multiError)
      }
      "NINO is invalid" in {
        val invalidNino = "GB123456E"
        mockAuth()
        setupMockGetCalculationListTYS(invalidNino, testTaxYearRange)(badRequestSingleError)

        val result = TestCalculationListController.getCalculationListTYS(invalidNino, testTaxYearRange)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson[errors.Error](InvalidNino)
      }
      "tax year is invalid" in {
        val invalidTaxYear = "23-25"
        mockAuth()
        setupMockGetCalculationListTYS(testNino, invalidTaxYear)(badRequestSingleError)

        val result = TestCalculationListController.getCalculationListTYS(testNino, invalidTaxYear)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson[errors.Error](InvalidTaxYear)
      }
    }
    "return 401 UNAUTHORIZED" when {
      "called with an unauthenticated user" in {
        mockAuth(Future.failed(new MissingBearerToken))

        val result = TestCalculationListController.getCalculationListTYS(testNino, testTaxYearRange)(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }
  }
}
