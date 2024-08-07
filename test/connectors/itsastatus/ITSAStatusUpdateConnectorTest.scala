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

import config.MicroserviceAppConfig
import connectors.itsastatus.ITSAStatusConnector.CorrelationIdHeader
import connectors.itsastatus.OptOutUpdateRequestModel._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{mock, reset, when}
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import play.mvc.Http.Status.{BAD_REQUEST, NO_CONTENT}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ITSAStatusUpdateConnectorTest extends AnyWordSpecLike with Matchers with BeforeAndAfter with ScalaFutures {

  val httpClient: HttpClient = mock(classOf[HttpClient])
  val appConfig: MicroserviceAppConfig = mock(classOf[MicroserviceAppConfig])
  implicit val headerCarrier: HeaderCarrier = mock(classOf[HeaderCarrier])
  val connector = new ITSAStatusConnector(httpClient, appConfig)

  val taxYear = "2023-24"
  val taxableEntityId: String = "AB123456A"

  before {
    reset(httpClient)
    reset(appConfig)
    reset(headerCarrier)

    stubApiToken("2149")
    when(appConfig.ifUrl).thenReturn(s"http://localhost:9084")
  }

  "For OptOutConnector.requestOptOutForTaxYear " when {

    "happy case" should {

      "return successful response" in {

        val apiRequest = OptOutUpdateRequest(taxYear, optOutUpdateReason)
        val apiResponse = OptOutUpdateResponseSuccess("123", NO_CONTENT)
        val httpResponse = HttpResponse(NO_CONTENT, Json.toJson(apiResponse), Map(CorrelationIdHeader -> Seq("123")))

        setupHttpClientMock[OptOutUpdateRequest](connector.buildUpdateRequestUrlWith(taxableEntityId))(apiRequest, httpResponse)

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

        setupHttpClientMock[OptOutUpdateRequest](connector.buildUpdateRequestUrlWith(taxableEntityId))(apiRequest, httpResponse)

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

        setupHttpClientMock[OptOutUpdateRequest](connector.buildUpdateRequestUrlWith(taxableEntityId))(apiRequest, httpResponse)

        val result: Future[OptOutUpdateResponse] = connector.requestOptOutForTaxYear(taxableEntityId, apiRequest)

        result.futureValue shouldBe OptOutUpdateResponseFailure("Unknown_CorrelationId", BAD_REQUEST, errorItems)

      }
    }
  }

  private def stubApiToken(api: String): Any = {
    when(appConfig.getIFHeaders(api)).thenReturn(apiHeaders(api))
  }

  private def apiHeaders(api: String): Seq[(String, String)] = {
    Seq(
      "Environment" -> (api + "-test-token"),
      "Authorization" -> ("Bearer " + api + "-test-token")
    )
  }

  def setupHttpClientMock[R](url: String, headers: Seq[(String, String)] = apiHeaders("2149"))(body: R, response: HttpResponse): Unit = {
    when(httpClient.PUT[R, HttpResponse](ArgumentMatchers.eq(url), ArgumentMatchers.eq(body), ArgumentMatchers.eq(headers))
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(response))
  }
}