/*
 * Copyright 2021 HM Revenue & Customs
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

import assets.BaseTestConstants.mtdRef
import assets.IncomeSourceDetailsTestConstants._
import mocks.MockIncomeSourceDetailsConnector
import models.core.NinoResponse
import models.incomeSourceDetails.IncomeSourceDetailsResponseModel
import utils.TestSupport

import scala.concurrent.Future

class IncomeSourceDetailsServiceSpec extends TestSupport with MockIncomeSourceDetailsConnector {

  object TestIncomeSourceDetailsService extends IncomeSourceDetailsService(mockIncomeSourceDetailsConnector)

  "The IncomeSourceDetailsService" when {

    "getIncomeSourceDetails method is called" when {

      def result: Future[IncomeSourceDetailsResponseModel] = TestIncomeSourceDetailsService.getIncomeSourceDetails(mtdRef)

      "a successful response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted IncomeSourceDetailsModel" in {
          val resp: IncomeSourceDetailsResponseModel = testIncomeSourceDetailsModel
          mockIncomeSourceDetailsResult(resp)
          result.futureValue shouldBe testIncomeSourceDetailsModel
        }
      }

      "an Error Response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted DesBusinessDetailsError model" in {
          mockIncomeSourceDetailsResult(testIncomeSourceDetailsError)
          result.futureValue shouldBe testIncomeSourceDetailsError
        }
      }
    }

    "getNino method is called" when {

      def result: Future[NinoResponse] = TestIncomeSourceDetailsService.getNino(mtdRef)

      "a successful response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted NinoModel" in {
          val resp: IncomeSourceDetailsResponseModel = testIncomeSourceDetailsModel
          mockIncomeSourceDetailsResult(resp)
          result.futureValue shouldBe testNinoModel
        }
      }

      "an Error Response is returned from the IncomeSourceDetailsConnector" should {

        "return a correctly formatted IncomeSourceDetailsError model" in {
          mockIncomeSourceDetailsResult(testIncomeSourceDetailsError)
          result.futureValue shouldBe testNinoError
        }
      }
    }
  }
}

