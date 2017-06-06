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

package connectors

import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.Json
import play.mvc.Http.Status
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}


class FinancialDataConnectorSpec extends UnitSpec with WithFakeApplication with MockHttp {

  implicit val hc = HeaderCarrier()

  val successResponse = HttpResponse(Status.OK, Some(Json.parse("{}")))
  val badResponse = HttpResponse(Status.BAD_REQUEST, responseString = Some("Error Message"))
  val expectedSuccess = SuccessResponse(Json.parse("{}"))
  val expectedBadResponse = ErrorResponse(Status.BAD_REQUEST, "Error Message")

  object TestFinancialDataConnector extends FinancialDataConnector(mockHttpGet)

  "FinancialDataConnector.getJsonBody" should {

    "return Status (OK) and a JSON body when successful as a SuccessResponse model" in {
      setupMockHttpGet(TestFinancialDataConnector.getFinancialDataUrl("1234"))(successResponse)
      val result = TestFinancialDataConnector.getFinancialData("1234")
      val enrolResponse = await(result)
      enrolResponse shouldBe expectedSuccess
    }

    "return ErrorResponse model in case of failure" in {
      setupMockHttpGet(TestFinancialDataConnector.getFinancialDataUrl("1234"))(badResponse)
      val result = TestFinancialDataConnector.getFinancialData("1234")
      val enrolResponse = await(result)
      enrolResponse shouldBe expectedBadResponse
    }

  }

}
