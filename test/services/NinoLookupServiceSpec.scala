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

package services

import assets.TestConstants.BusinessDetails._
import assets.TestConstants._
import mocks.MockNinoLookupConnector
import models._
import utils.TestSupport

import scala.concurrent.Future

class NinoLookupServiceSpec extends TestSupport with MockNinoLookupConnector {

  object TestNinoLookupService extends NinoLookupService(mockNinoLookupConnector)
  def result: Future[DesResponseModel] = TestNinoLookupService.getNino(mtdRef)

  "The NinoLookupService.getNino method" when {

    "a successful response is returned from the NinoLookupConnector" should {

      "return a correctly formatted DesBusinessDetails model" in {
        val resp: DesResponseModel = desBusinessResponse(testBusinessModel)
        mockDesBusinessDetailsResult(resp)
        await(result) shouldBe testNinoModel
      }
    }

    "an Error Response is returned from the NinoLookupConnector" should {

      "return a correctly formatted DesBusinessDetailsError model" in {
        mockDesBusinessDetailsResult(testDesResponseError)
        await(result) shouldBe testDesResponseError
      }
    }
  }
}
