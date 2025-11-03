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

import constants.BaseTestConstants.testNino
import constants.HipIncomeSourceDetailsTestConstants
import mocks.MockBusinessDetailsConnector
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import utils.TestSupport

import scala.concurrent.Future

class BusinessDetailsServiceSpec extends TestSupport with MockBusinessDetailsConnector {

  object TestBusinessDetailsService extends BusinessDetailsService(mockGetBusinessDetailsConnector)

  "The BusinessDetailsService" when {

    "getBusinessDetails(Hip) method is called" when {

      def result: Future[Result] = TestBusinessDetailsService.getBusinessDetails(testNino)

      "a successful response is returned from the BusinessDetailsConnector" should {

        "return a correctly formatted IncomeSourceDetailsModel" in {
          val resp: models.hip.incomeSourceDetails.IncomeSourceDetailsResponseModel =
            HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsModel
          mockGetBusinessDetailsResult(resp, models.hip.incomeSourceDetails.Nino)
          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe Json.toJson(HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsModel)
        }
      }

      "an Error Response is returned from the BusinessDetailsConnector" should {

        "return a correctly formatted error model" in {
          mockGetBusinessDetailsResult(
            HipIncomeSourceDetailsTestConstants.testIncomeSourceDetailsError,
            models.hip.incomeSourceDetails.Nino
          )
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
