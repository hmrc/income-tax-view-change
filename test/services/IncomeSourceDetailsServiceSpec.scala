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

package services

import config.MicroserviceAppConfig
import constants.BaseTestConstants.mtdRef
import constants.HipIncomeSourceDetailsTestConstants
import constants.IncomeSourceDetailsTestConstants._
import mocks.{MockBusinessDetailsConnector, MockGetBusinessDetailsConnector}
import models.hip.GetBusinessDetailsHipApi
import models.incomeSourceDetails.{IncomeSourceDetailsResponseModel, MtdId}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import utils.TestSupport

import scala.concurrent.Future

class IncomeSourceDetailsServiceSpec extends TestSupport with MockBusinessDetailsConnector with MockGetBusinessDetailsConnector {

  val mockAppConfig = mock[MicroserviceAppConfig]
  object TestIncomeSourceDetailsService extends IncomeSourceDetailsService(mockBusinessDetailsConnector, mockGetBusinessDetailsConnector, mockAppConfig)

  "The IncomeSourceDetailsService" when {

    "getIncomeSourceDetails method is called with IFConnector when Hip FS is disabled" when {

      def result: Future[Result] = TestIncomeSourceDetailsService.getIncomeSourceDetails(mtdRef)

      "a successful response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted IncomeSourceDetailsModel" in {
          val resp: IncomeSourceDetailsResponseModel = testIncomeSourceDetailsModel
          when(mockAppConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi)).thenReturn(false)
          mockGetBusinessDetailsResult(resp, MtdId)
          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe Json.toJson(testIncomeSourceDetailsModel)
        }
      }

      "an Error Response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted DesBusinessDetailsError model" in {
          when(mockAppConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi)).thenReturn(false)
          mockGetBusinessDetailsResult(testIncomeSourceDetailsError, MtdId)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.toJson(testIncomeSourceDetailsError)
        }
      }
    }

    "getIncomeSourceDetails method is called with HipConnector when Hip FS is enabled" when {

      def result: Future[Result] = TestIncomeSourceDetailsService.getIncomeSourceDetails(mtdRef)

      "a successful response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted IncomeSourceDetailsModel" in {
          val resp: models.hip.incomeSourceDetails.IncomeSourceDetailsResponseModel = HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsModel
          when(mockAppConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi)).thenReturn(true)
          mockHipGetBusinessDetailsResult(resp, models.hip.incomeSourceDetails.MtdId)
          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe Json.toJson(HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsModel)
        }
      }

      "an Error Response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted DesBusinessDetailsError model" in {
          when(mockAppConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi)).thenReturn(true)
          mockHipGetBusinessDetailsResult(HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsError, models.hip.incomeSourceDetails.MtdId)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.toJson(HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsError)
        }
      }
    }

    "getNino method is called when Hip Api is disabled" when {

      def result: Future[Result] = TestIncomeSourceDetailsService.getNino(mtdRef)

      "a successful response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted NinoModel" in {
          when(mockAppConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi)).thenReturn(false)
          val resp: IncomeSourceDetailsResponseModel = testIncomeSourceDetailsModel
          mockGetBusinessDetailsResult(resp, MtdId)
          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe Json.toJson(testNinoModel)
        }
      }

      "an Error Response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted IncomeSourceDetailsError model" in {
          when(mockAppConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi)).thenReturn(false)
          mockGetBusinessDetailsResult(testIncomeSourceDetailsError, MtdId)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.toJson(testNinoError)
        }
      }
    }

    "getNino method is called when Hip Api is enabled" when {

      def result: Future[Result] = TestIncomeSourceDetailsService.getNino(mtdRef)

      "a successful response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted NinoModel" in {
          when(mockAppConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi)).thenReturn(true)
          val resp: models.hip.incomeSourceDetails.IncomeSourceDetailsResponseModel = HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsModel
          mockHipGetBusinessDetailsResult(resp, models.hip.incomeSourceDetails.MtdId)
          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe Json.toJson(HipIncomeSourceDetailsTestConstants.testNinoModel)
        }
      }

      "an Error Response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted IncomeSourceDetailsError model" in {
          when(mockAppConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi)).thenReturn(true)
          mockHipGetBusinessDetailsResult(HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsError, models.hip.incomeSourceDetails.MtdId)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.toJson(HipIncomeSourceDetailsTestConstants.testNinoError)
        }
      }
    }
  }
}

