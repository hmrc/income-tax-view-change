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

import mocks.MockHttp
import assets.BaseTestConstants.testNino
import assets.ReportDeadlinesTestConstants._
import models.reportDeadlines.ReportDeadlinesErrorModel
import play.mvc.Http.Status
import uk.gov.hmrc.http.logging.Authorization
import utils.TestSupport
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

class PropertyDeadlinesConnectorSpec extends TestSupport with MockHttp {

  object TestPropertyDeadlinesConnector extends PropertyDeadlinesConnector(mockHttpGet, microserviceAppConfig)

  "FinancialDataConnector.getFinancialData" should {

    import TestPropertyDeadlinesConnector._

    lazy val expectedHc: HeaderCarrier =
      hc.copy(authorization =Some(Authorization(s"Bearer ${appConfig.desToken}"))).withExtraHeaders("Environment" -> appConfig.desEnvironment)

    def mock: (HttpResponse) => Unit =
      setupMockHttpGetWithHeaderCarrier(getPropertyDeadlinesUrl(testNino), expectedHc)(_)

    "return Status (OK) and a JSON body when successful as a LatTaxCalculation model" in {
      mock(successResponse)
      await(getPropertyDeadlines(testNino)) shouldBe testReportDeadlines
    }

    "return LastTaxCalculationError model in case of failure" in {
      mock(badResponse)
      await(getPropertyDeadlines(testNino)) shouldBe ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, "Error Message")
    }

    "return LastTaxCalculationError model in case of bad JSON" in {
      mock(badJson)
      await(getPropertyDeadlines(testNino)) shouldBe
        ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Property Deadlines Data ")
    }

    "return LastTaxCalculationError model in case of failed future" in {
      setupMockHttpGetFailed(getPropertyDeadlinesUrl(testNino))
      await(getPropertyDeadlines(testNino)) shouldBe
        ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, "Unexpected failed future")
    }
  }

}
