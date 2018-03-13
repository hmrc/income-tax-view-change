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
import models.IncomeSourceDetailsError
import play.mvc.Http.Status
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.TestSupport

class IncomeSourceDetailsConnectorSpec extends TestSupport with MockHttp {

  object TestIncomeSourceDetailsConnector extends IncomeSourceDetailsConnector(mockHttpGet, microserviceAppConfig)

  "NinoLookupConnector.getIncomeSourceDetails" should {

    import TestIncomeSourceDetailsConnector._

    lazy val expectedHc: HeaderCarrier =
      hc.copy(authorization =Some(Authorization(s"Bearer ${appConfig.desToken}"))).withExtraHeaders("Environment" -> appConfig.desEnvironment)

    def mock: (HttpResponse) => Unit =
      setupMockHttpGetWithHeaderCarrier(getIncomeSourceDetailsUrl(mtdRef), expectedHc)(_)

    "return Status (OK) and a JSON body when successful as a DesBusinessDetails" in {
      mock(successResponse)
      await(getIncomeSourceDetails(mtdRef)) shouldBe testIncomeSourceDetailsModel
    }

    "return LastTaxCalculationError model in case of failure" in {
      mock(badResponse)
      await(getIncomeSourceDetails(mtdRef)) shouldBe IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")
    }

    "return LastTaxCalculationError model in case of bad JSON" in {
      mock(badJson)
      await(getIncomeSourceDetails(mtdRef)) shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Des Business Details")
    }

    "return LastTaxCalculationError model in case of failed future" in {
      setupMockHttpGetFailed(getIncomeSourceDetailsUrl(mtdRef))
      await(getIncomeSourceDetails(mtdRef)) shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future")
    }
  }
}
