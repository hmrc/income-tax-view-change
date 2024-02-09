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

package connectors

import assets.BaseTestConstants._
import assets.IncomeSourceDetailsTestConstants._
import mocks.MockHttp
import models.incomeSourceDetails.{Nino, MtdId, IncomeSourceDetailsError, IncomeSourceDetailsNotFound}
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class GetBusinessDetailsConnectorSpec extends TestSupport with MockHttp {

  object TestGetBusinessDetailsConnector extends GetBusinessDetailsConnector(mockHttpGet, microserviceAppConfig)

  import TestGetBusinessDetailsConnector._

  "GetBusinessDetailsConnector.getBusinessDetails(BusinessAccess)" should {

    lazy val mock: HttpResponse => Unit = setupMockHttpGetWithHeaderCarrier(getUrl(Nino, testNino), microserviceAppConfig.getIFHeaders("1171"))(_)

    "return Status (OK) and a JSON body when successful as a DesBusinessDetails" in {
      mock(successResponse)
      getBusinessDetails(testNino, Nino).futureValue shouldBe testIncomeSourceDetailsModel
    }

    "return LastTaxCalculationError model in case of failure" in {
      mock(badResponse)
      getBusinessDetails(testNino, Nino).futureValue shouldBe IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")
    }

    "return LastTaxCalculationError model with status 404 in case of failure" in {
      mock(notFoundBadResponse)
      getBusinessDetails(testNino, Nino).futureValue shouldBe IncomeSourceDetailsNotFound(Status.NOT_FOUND, "Dummy error message")
    }

    "return LastTaxCalculationError model in case of bad JSON" in {
      mock(badJson)
      getBusinessDetails(testNino, Nino).futureValue shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Business Details")
    }

    "return LastTaxCalculationError model in case of failed future" in {
      setupMockHttpGetFailed(getUrl(Nino,testNino))
      getBusinessDetails(testNino, Nino).futureValue shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, error")
    }
  }
  "GetBusinessDetailsConnector.getBusinessDetails(IncomeSourceAccess)" should {

    lazy val mock: HttpResponse => Unit = setupMockHttpGetWithHeaderCarrier(getUrl(MtdId, mtdRef), microserviceAppConfig.getIFHeaders("1171"))(_)

    "return Status (OK) and a JSON body when successful as a DesBusinessDetails" in {
      mock(successResponse)
      getBusinessDetails(mtdRef, MtdId).futureValue shouldBe testIncomeSourceDetailsModel
    }

    "return LastTaxCalculationError model in case of failure" in {
      mock(badResponse)
      getBusinessDetails(mtdRef, MtdId).futureValue shouldBe IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")
    }

    "return LastTaxCalculationError model in case of bad JSON" in {
      mock(badJson)
      getBusinessDetails(mtdRef, MtdId).futureValue shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Business Details")
    }

    "return LastTaxCalculationError model in case of failed future" in {
      setupMockHttpGetFailed(getUrl(MtdId, mtdRef))
      getBusinessDetails(mtdRef, MtdId).futureValue shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, error")
    }
  }
}
