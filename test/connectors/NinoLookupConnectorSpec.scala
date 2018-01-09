/*
 * Copyright 2018 HM Revenue & Customs
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

package connectors

import assets.TestConstants.BusinessDetails._
import assets.TestConstants._
import mocks.MockHttp
import models.DesBusinessDetailsError
import play.mvc.Http.Status
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.TestSupport

class NinoLookupConnectorSpec extends TestSupport with MockHttp {

  object TestNinoLookupConnector extends NinoLookupConnector(mockHttpGet, microserviceAppConfig)

  "NinoLookupConnecytor.getDesBusinessDetails" should {

    import TestNinoLookupConnector._

    lazy val expectedHc: HeaderCarrier =
      hc.copy(authorization =Some(Authorization(s"Bearer ${appConfig.desToken}"))).withExtraHeaders("Environment" -> appConfig.desEnvironment)

    def mock: (HttpResponse) => Unit =
      setupMockHttpGetWithHeaderCarrier(getDesBusinessDetailsUrl(mtdRef), expectedHc)(_)

    "return Status (OK) and a JSON body when successful as a DesBusinessDetails" in {
      mock(successResponse)
      await(getDesBusinessDetails(mtdRef)) shouldBe desBusinessResponse(testBusinessModel)
    }

    "return LastTaxCalculationError model in case of failure" in {
      mock(badResponse)
      await(getDesBusinessDetails(mtdRef)) shouldBe testDesResponseError
    }

    "return LastTaxCalculationError model in case of bad JSON" in {
      mock(badJson)
      await(getDesBusinessDetails(mtdRef)) shouldBe
        DesBusinessDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Des Business Details")
    }

    "return LastTaxCalculationError model in case of failed future" in {
      setupMockHttpGetFailed(getDesBusinessDetailsUrl(mtdRef))
      await(getDesBusinessDetails(mtdRef)) shouldBe
        DesBusinessDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future")
    }
  }
}
