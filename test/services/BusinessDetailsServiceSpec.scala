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
import constants.IncomeSourceDetailsTestConstants._
import mocks.MockBusinessDetailsConnector
import models.incomeSourceDetails.{Nino, IncomeSourceDetailsResponseModel}
import utils.TestSupport

import scala.concurrent.Future

class BusinessDetailsServiceSpec extends TestSupport with MockBusinessDetailsConnector {

  object TestBusinessDetailsService extends BusinessDetailsService(mockBusinessDetailsConnector)

  "The BusinessDetailsService" when {

    "getBusinessDetails method is called" when {

      def result: Future[IncomeSourceDetailsResponseModel] = TestBusinessDetailsService.getBusinessDetails(testNino)

      "a successful response is returned from the BusinessDetailsConnector" should {

        "return a correctly formatted IncomeSourceDetailsModel" in {
          val resp: IncomeSourceDetailsResponseModel = testIncomeSourceDetailsModel
          mockGetBusinessDetailsResult(resp, Nino)
          result.futureValue shouldBe testIncomeSourceDetailsModel
        }
      }

      "an Error Response is returned from the BusinessDetailsConnector" should {

        "return a correctly formatted DesBusinessDetailsError model" in {
          mockGetBusinessDetailsResult(testIncomeSourceDetailsError, Nino)
          result.futureValue shouldBe testIncomeSourceDetailsError
        }
      }
    }
  }
}
