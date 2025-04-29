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

package connectors.hip

import constants.BaseTestConstants._
import constants.HipIncomeSourceDetailsTestConstants._
import mocks.MockHttpV2
import models.hip.GetBusinessDetailsHipApi
import models.hip.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsNotFound, MtdId, Nino}
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse
import utils.TestSupport

class GetBusinessDetailsConnectorSpec extends TestSupport with MockHttpV2 {

  object TestGetBusinessDetailsConnector extends GetBusinessDetailsConnector(mockHttpClientV2, microserviceAppConfig)

  import TestGetBusinessDetailsConnector._

  "GetBusinessDetailsConnector.getBusinessDetails(BusinessAccess)" should {

    lazy val mock: HttpResponse => Unit = setupMockHttpGetWithHeaderCarrier(
      getBusinessDetailsUrl(Nino, testNino), microserviceAppConfig.getHIPHeaders(GetBusinessDetailsHipApi))(_)

    "return Status (OK) and a JSON body when successful as a GetBusinessDetails" in {
      mock(successResponse)
      getHeaders.exists(_._1 == "Authorization") shouldBe true
      getHeaders.exists(_._1 == "correlationId") shouldBe true
      getHeaders.exists(_._1 == "X-Message-Type") shouldBe true
      getHeaders.exists(_._1 == "X-Message-Type") shouldBe true
      getHeaders.exists(_._1 == "X-Message-Type") shouldBe true
      getHeaders.exists(_._1 == "X-Message-Type") shouldBe true
      getBusinessDetails(testNino, Nino).futureValue shouldBe testIncomeSourceDetailsModel

    }

    "return IncomeSourceDetailsError model in case of failure" in {
      mock(badResponse)
      getBusinessDetails(testNino, Nino).futureValue shouldBe IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")
    }

    "return IncomeSourceDetailsNotFound model with status 404 in case of failure" in {
      mock(notFoundBadResponse)
      getBusinessDetails(testNino, Nino).futureValue shouldBe IncomeSourceDetailsNotFound(Status.NOT_FOUND, "Dummy error message")
    }

    "return IncomeSourceDetailsError model in case of bad JSON" in {
      mock(badJson)
      getBusinessDetails(testNino, Nino).futureValue shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Business Details")
    }

    "return IncomeSourceDetailsError model in case of failed future" in {
      setupMockFailedHttpV2Get(getBusinessDetailsUrl(Nino,testNino))
      getBusinessDetails(testNino, Nino).futureValue shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, error")
    }
  }
  "GetBusinessDetailsConnector.getBusinessDetails(IncomeSourceAccess)" should {

    lazy val mock: HttpResponse => Unit = setupMockHttpGetWithHeaderCarrier(getBusinessDetailsUrl(MtdId, mtdRef), microserviceAppConfig.getIFHeaders("1171"))(_)

    "return Status (OK) and a JSON body when successful as a GetBusinessDetails" in {
      mock(successResponse)
      getBusinessDetails(mtdRef, MtdId).futureValue shouldBe testIncomeSourceDetailsModel
    }

    "return IncomeSourceDetailsError model in case of failure" in {
      mock(badResponse)
      getBusinessDetails(mtdRef, MtdId).futureValue shouldBe IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")
    }

    "return IncomeSourceDetailsError model in case of bad JSON" in {
      mock(badJson)
      getBusinessDetails(mtdRef, MtdId).futureValue shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Business Details")
    }

    "return IncomeSourceDetailsError model in case of failed future" in {
      setupMockFailedHttpV2Get(getBusinessDetailsUrl(MtdId, mtdRef))
      getBusinessDetails(mtdRef, MtdId).futureValue shouldBe
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, error")
    }
  }
}
