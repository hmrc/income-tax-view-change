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

package connectors.itsastatus

import connectors.itsastatus.ITSAStatusConnector.CorrelationIdHeader
import connectors.itsastatus.OptOutUpdateRequestModel._
import mocks.MockHttpV2
import org.mockito.Mockito.mock
import play.api.libs.json.Json
import play.mvc.Http.Status.{BAD_REQUEST, NO_CONTENT}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.TestSupport

import scala.concurrent.Future

class ITSAStatusUpdateConnectorTest extends TestSupport with MockHttpV2 {

  implicit val headerCarrier: HeaderCarrier = mock(classOf[HeaderCarrier])
  val connector = new ITSAStatusConnector(mockHttpClientV2, microserviceAppConfig)
  val taxYear = "23-24"
  val taxableEntityId: String = "AB123456A"
  val url: String = s"${microserviceAppConfig.ifUrl}/income-tax/itsa-status/update/$taxableEntityId"

  "For OptOutConnector.requestOptOutForTaxYear " when {

    "happy case" should {

      "return successful response" in {

        val apiRequest = OptOutUpdateRequest(taxYear, optOutUpdateReason)
        val apiResponse = OptOutUpdateResponseSuccess("123", NO_CONTENT)
        val httpResponse = HttpResponse(NO_CONTENT, Json.toJson(apiResponse), Map(CorrelationIdHeader -> Seq("123")))

        setupMockHttpV2PutWithHeaderCarrier(url)(httpResponse)

        val result: Future[OptOutUpdateResponse] = connector.requestOptOutForTaxYear(taxableEntityId, apiRequest)

        result.futureValue shouldBe OptOutUpdateResponseSuccess("123", NO_CONTENT)

      }
    }

    "unhappy case" should {

      "return failure response" in {

        val errorItems = List(ErrorItem("INVALID_TAXABLE_ENTITY_ID",
          "Submission has not passed validation. Invalid parameter taxableEntityId."))
        val correlationId = "123"
        val apiRequest = OptOutUpdateRequest(taxYear, optOutUpdateReason)
        val apiFailResponse = OptOutUpdateResponseFailure(correlationId, BAD_REQUEST, errorItems)
        val httpResponse = HttpResponse(BAD_REQUEST, Json.toJson(apiFailResponse), Map(CorrelationIdHeader -> Seq("123")))

        setupMockHttpV2PutWithHeaderCarrier(url)(httpResponse)

        val result: Future[OptOutUpdateResponse] = connector.requestOptOutForTaxYear(taxableEntityId, apiRequest)

        result.futureValue shouldBe OptOutUpdateResponseFailure(correlationId, BAD_REQUEST, errorItems)

      }
    }

    "unhappy case, missing header" should {

      "return failure response" in {

        val errorItems = List(ErrorItem("INVALID_TAXABLE_ENTITY_ID",
          "Submission has not passed validation. Invalid parameter taxableEntityId."))
        val correlationId = "123"
        val apiRequest = OptOutUpdateRequest(taxYear, optOutUpdateReason)
        val apiFailResponse = OptOutUpdateResponseFailure(correlationId, BAD_REQUEST, errorItems)
        val httpResponse = HttpResponse(BAD_REQUEST, Json.toJson(apiFailResponse), Map.empty)

        setupMockHttpV2PutWithHeaderCarrier(url)(httpResponse)

        val result: Future[OptOutUpdateResponse] = connector.requestOptOutForTaxYear(taxableEntityId, apiRequest)

        result.futureValue shouldBe OptOutUpdateResponseFailure("Unknown_CorrelationId", BAD_REQUEST, errorItems)

      }
    }
  }

}