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
import constants.CalculationListDesTestConstants.{badRequestMultiError, badRequestSingleError, multiError, singleError}
import constants.CalculationListTestConstants.{calculationListFull, invalidNinoResponse, invalidTaxYearResponse}
import config.MicroserviceAppConfig
import connectors.hip.CalculationListLegacyConnector
import controllers.predicates.AuthenticationPredicate
import mocks.{MockCalculationListService, MockMicroserviceAuthConnector}
import models.calculationList.CalculationListResponseModel
import models.errors
import models.errors.{InvalidNino, InvalidTaxYear}
import models.hip.{ErrorResponse, FailureResponse, GetLegacyCalcListApiName, OriginWithErrorCodeAndResponse}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status, stubControllerComponents}
import uk.gov.hmrc.auth.core.MissingBearerToken

import scala.concurrent.Future

class CalculationListControllerSpec extends ControllerBaseSpec with MockMicroserviceAuthConnector with MockCalculationListService {
  val mockCC: ControllerComponents = stubControllerComponents()
  val mockHipCalcListConnector = mock[CalculationListLegacyConnector]
  val mockAppConfig = mock[MicroserviceAppConfig]
  val authPredicate = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig)
  val successResponseDes: Either[Nothing, CalculationListResponseModel] = Right(constants.CalculationListDesTestConstants.calculationListFull)
  val successResponseHip: Either[Nothing, CalculationListResponseModel] = Right(constants.CalculationListTestConstants.calculationListFull)

  object TestCalculationListController extends CalculationListController(
    authPredicate, mockCalculationListService, mockHipCalcListConnector, mockAppConfig, mockCC)

  "CalculationListController.getCalculationList from DES" should {
    "return 200 OK" when {
      "user is authenticated and CalculationListService returns a success response" in {
        when(mockAppConfig.hipFeatureSwitchEnabled(GetLegacyCalcListApiName())).thenReturn(false)
        mockAuth()
        setupMockGetCalculationList(testNino, testTaxYearEnd)(successResponseDes)

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(constants.CalculationListDesTestConstants.calculationListFull.calculations.head)
      }
    }
    "return 400 BAD_REQUEST" when {
      "CalculationListService returns a single 400 BAD_REQUEST error" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(false)
        mockAuth()
        setupMockGetCalculationList(testNino, testTaxYearEnd)(constants.CalculationListDesTestConstants.badRequestSingleError)

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(constants.CalculationListDesTestConstants.singleError)
      }
      "CalculationListService returns multiple errors" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(false)
        mockAuth()
        setupMockGetCalculationList(testNino, testTaxYearEnd)(constants.CalculationListDesTestConstants.badRequestMultiError)

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(constants.CalculationListDesTestConstants.multiError)
      }
      "NINO is invalid" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(false)
        val invalidNino = "GB123456E"
        mockAuth()
        setupMockGetCalculationList(invalidNino, testTaxYearEnd)(constants.CalculationListDesTestConstants.badRequestSingleError)

        val result = TestCalculationListController.getCalculationList(invalidNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson[errors.Error](InvalidNino)
      }
      "tax year is invalid" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(false)
        val invalidTaxYear = "3000"
        mockAuth()
        setupMockGetCalculationList(testNino, invalidTaxYear)(constants.CalculationListDesTestConstants.badRequestSingleError)

        val result = TestCalculationListController.getCalculationListTYS(testNino, invalidTaxYear)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson[errors.Error](InvalidTaxYear)
      }
    }
    "return 401 UNAUTHORIZED" when {
      "called with an unauthenticated user" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(false)
        mockAuth(Future.failed(new MissingBearerToken))

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }
  }

  "CalculationListController.getCalculationList from HIP" should {
    "return 200 OK" when {
      "user is authenticated and CalculationListService returns a success response" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(true)
        mockAuth()
        when(mockHipCalcListConnector.getCalculationList(
          ArgumentMatchers.eq(testNino), ArgumentMatchers.eq(testTaxYearEnd))(any(), any())).thenReturn(Future.successful(successResponseHip))

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(calculationListFull.calculations.head)
      }
    }
    "return 400 BAD_REQUEST" when {
      "CalculationListService returns a single 400 BAD_REQUEST error" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(true)
        mockAuth()
        when(mockHipCalcListConnector.getCalculationList(
          ArgumentMatchers.eq(testNino), ArgumentMatchers.eq(testTaxYearEnd))(any(), any())).thenReturn(
          Future.successful(Left(ErrorResponse(BAD_REQUEST, Json.toJson("{}")))))

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
      }

      "NINO is invalid from backend" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(true)
        mockAuth()
        when(mockHipCalcListConnector.getCalculationList(
          ArgumentMatchers.eq(testNino), ArgumentMatchers.eq(testTaxYearEnd))(any(), any())).thenReturn(
          Future.successful(Left(ErrorResponse(BAD_REQUEST, invalidNinoResponse))))

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson[OriginWithErrorCodeAndResponse](
          OriginWithErrorCodeAndResponse("HIP", Seq(FailureResponse("1215", "Invalid taxable entity id"))))
      }
      "tax year is invalid from backend" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(true)
        mockAuth()
        when(mockHipCalcListConnector.getCalculationList(
          ArgumentMatchers.eq(testNino), ArgumentMatchers.eq(testTaxYearEnd))(any(), any())).thenReturn(
          Future.successful(Left(ErrorResponse(BAD_REQUEST, invalidTaxYearResponse))))

        val result = TestCalculationListController.getCalculationList(testNino, testTaxYearEnd)(fakeRequest)
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson[OriginWithErrorCodeAndResponse](
          OriginWithErrorCodeAndResponse("HIP", Seq(FailureResponse("1117", "The tax year provided is invalid"))))
      }
    }
    "return 401 UNAUTHORIZED" when {
      "called with an unauthenticated user" in {
        when(mockAppConfig.hipFeatureSwitchEnabled("get-legacy-calc-list-1404")).thenReturn(true)
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
        setupMockGetCalculationListTYS(testNino, testTaxYearRange)(successResponseDes)

        val result = TestCalculationListController.getCalculationListTYS(testNino, testTaxYearRange)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(constants.CalculationListDesTestConstants.calculationListFull.calculations.head)
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
