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

import controllers.predicates.AuthenticationPredicate
import mocks.{MockCreateBusinessDetailsService, MockMicroserviceAuthConnector}
import models.hip.createIncomeSource._
import models.hip.incomeSourceDetails.{CreateBusinessDetailsHipErrorResponse, IncomeSource}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status, stubControllerComponents}

import java.time.LocalDate

class CreateBusinessDetailsControllerSpec extends ControllerBaseSpec with MockMicroserviceAuthConnector with MockCreateBusinessDetailsService {
  val mockCC: ControllerComponents = stubControllerComponents()
  val authPredicate = new AuthenticationPredicate(mockMicroserviceAuthConnector, mockCC, microserviceAppConfig)
  val testDate: String = LocalDate.of(2022, 5, 1).toString
  val mtdbsa: String = "XAIT12345678910"

  object TestCreateBusinessDetailsController extends CreateBusinessDetailsController(authPredicate, mockCreateBusinessDetailsService, mockCC)


  "CreateBusinessDetailsController.createBusinessDetails" should {
    s"return $INTERNAL_SERVER_ERROR" when {
      "user is authenticated and CreateBusinessDetailsService returns an error response" in {
        mockAuth()
        mockCreateBusinessDetailsErrorResponse()

        val result = TestCreateBusinessDetailsController.createBusinessDetails()(
          fakePostRequest.withJsonBody(
            Json.toJson(
              CreateForeignPropertyIncomeSourceHipRequest(mtdbsa,
                PropertyDetails(Some(testDate), Some("A"), testDate)
              )
            )
          )
        )

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe
          Json.toJson(
            CreateBusinessDetailsHipErrorResponse(INTERNAL_SERVER_ERROR, "failed to create income source")
          )
      }
    }
    s"return $BAD_REQUEST" when {
      "an invalid json body is sent" in {
        mockAuth()

        val result = TestCreateBusinessDetailsController.createBusinessDetails()(
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

        val result = TestCreateBusinessDetailsController.createBusinessDetails()(
          fakePostRequest.withJsonBody(
            Json.toJson(
              CreateBusinessIncomeSourceHipRequest(mtdbsa,
                List(
                  BusinessDetails(
                    accountingPeriodStartDate = testDate,
                    accountingPeriodEndDate = testDate,
                    tradingName = "Big Business",
                    address = AddressDetails("10 FooBar Street", None, None, None, "GB", None),
                    typeOfBusiness = "",
                    tradingStartDate = testDate,
                    cashAccrualsFlag = Some("C"),
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

        val result = TestCreateBusinessDetailsController.createBusinessDetails()(
          fakePostRequest.withJsonBody(
            Json.toJson(
              CreateForeignPropertyIncomeSourceHipRequest(mtdbsa,
                PropertyDetails(Some(testDate), Some("A"), testDate)
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

      "a CreateForeignPropertyIncomeSourceRequest model as json body is sent with no cash or accurals flag" in {
        mockAuth()
        mockCreateIncomeSourceSuccessResponse()

        val result = TestCreateBusinessDetailsController.createBusinessDetails()(
          fakePostRequest.withJsonBody(
            Json.toJson(
              CreateForeignPropertyIncomeSourceHipRequest(mtdbsa,
                PropertyDetails(Some(testDate), None, testDate)
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

        val result = TestCreateBusinessDetailsController.createBusinessDetails()(
          fakePostRequest.withJsonBody(
            Json.toJson(
              CreateUKPropertyIncomeSourceHipRequest(mtdbsa,
                PropertyDetails(Some(testDate), Some("C"), testDate)
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