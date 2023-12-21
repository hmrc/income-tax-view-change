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

import assets.BaseTestConstants.mtdRef
import controllers.predicates.AuthenticationPredicate
import mocks.{MockCreateBusinessDetailsService, MockMicroserviceAuthConnector}
import models.createIncomeSource._
import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.{CreateBusinessDetailsErrorResponse, IncomeSource}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status, stubControllerComponents}

import java.time.LocalDate

class CreateBusinessDetailsControllerSpec extends ControllerBaseSpec with MockMicroserviceAuthConnector with MockCreateBusinessDetailsService {
  val mockCC: ControllerComponents = stubControllerComponents()
  val authPredicate = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig)
  val testDate = LocalDate.of(2022, 5, 1).toString


  object TestCreateBusinessDetailsController extends CreateBusinessDetailsController(authPredicate, mockCreateBusinessDetailsService, mockCC)


  "CreateBusinessDetailsController.createBusinessDetails" should {
    s"return $INTERNAL_SERVER_ERROR" when {
      "user is authenticated and CreateBusinessDetailsService returns an error response" in {
        mockAuth()
        mockCreateBusinessDetailsErrorResponse()

        val result = TestCreateBusinessDetailsController.createBusinessDetails(mtdRef)(
          fakePostRequest.withJsonBody(
            Json.toJson(
              CreateForeignPropertyIncomeSourceRequest(
                PropertyDetails(testDate, "ACCRUALS", testDate)
              )
            )
          )
        )

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe
          Json.toJson(
            CreateBusinessDetailsErrorResponse(INTERNAL_SERVER_ERROR, "failed to create income source")
          )
      }
    }
    s"return $BAD_REQUEST" when {
      "an invalid json body is sent" in {
        mockAuth()

        val result = TestCreateBusinessDetailsController.createBusinessDetails(mtdRef)(
          fakePostRequest.withJsonBody(
            Json.obj()
          )
        )

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe
          Json.toJson(
            CreateBusinessDetailsRequestError("Json validation error while parsing request")
          )
      }
    }
    s"return $OK" when {
      "a CreateBusinessIncomeSourceRequest model as json body is sent" in {
        mockAuth()
        mockCreateIncomeSourceSuccessResponse()

        val result = TestCreateBusinessDetailsController.createBusinessDetails(mtdRef)(
          fakePostRequest.withJsonBody(
            Json.toJson(
              CreateBusinessIncomeSourceRequest(
                List(
                  BusinessDetails(
                    accountingPeriodStartDate = testDate,
                    accountingPeriodEndDate = testDate,
                    tradingName = "Big Business",
                    addressDetails = AddressDetails("10 FooBar Street", None, None, None, None, None),
                    typeOfBusiness = None,
                    tradingStartDate = testDate,
                    cashOrAccrualsFlag = "CASH",
                    cessationDate = None,
                    cessationReason = None
                  )
                )
              )
            )
          )
        )

        status(result) shouldBe OK
        contentAsJson(result) shouldBe
          Json.toJson(
            List(IncomeSource(testIncomeSourceId))
          )
      }
      "a CreateForeignPropertyIncomeSourceRequest model as json body is sent" in {
        mockAuth()
        mockCreateIncomeSourceSuccessResponse()

        val result = TestCreateBusinessDetailsController.createBusinessDetails(mtdRef)(
          fakePostRequest.withJsonBody(
            Json.toJson(
              CreateForeignPropertyIncomeSourceRequest(
                PropertyDetails(testDate, "ACCRUALS", testDate)
              )
            )
          )
        )

        status(result) shouldBe OK
        contentAsJson(result) shouldBe
          Json.toJson(
            List(IncomeSource(testIncomeSourceId))
          )
      }
      "a CreateUKPropertyIncomeSourceRequest model as json body is sent" in {
        mockAuth()
        mockCreateIncomeSourceSuccessResponse()

        val result = TestCreateBusinessDetailsController.createBusinessDetails(mtdRef)(
          fakePostRequest.withJsonBody(
            Json.toJson(
              CreateUKPropertyIncomeSourceRequest(
                PropertyDetails(testDate, "CASH", testDate)
              )
            )
          )
        )

        status(result) shouldBe OK
        contentAsJson(result) shouldBe
          Json.toJson(
            List(IncomeSource(testIncomeSourceId))
          )
      }
    }
  }
}