/*
 * Copyright 2024 HM Revenue & Customs
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

import config.MicroserviceAppConfig
import connectors.httpParsers.ClaimToAdjustPoaHttpParser._
import models.claimToAdjustPoa.ClaimToAdjustPoaRequest
import models.claimToAdjustPoa.ClaimToAdjustPoaResponse.{ClaimToAdjustPoaResponse, ErrorResponse}
import play.api.Logger
import play.api.http.Status.INTERNAL_SERVER_ERROR
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import java.util.UUID.randomUUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class ClaimToAdjustPoaConnector @Inject() ( val appConfig: MicroserviceAppConfig,
                                            val http: HttpClientV2)
                                          ( implicit val ec: ExecutionContext ) {

  val endpoint = s"${appConfig.ifUrl}/income-tax/calculations/POA/ClaimToAdjust"

  def postClaimToAdjustPoa(request: ClaimToAdjustPoaRequest)(implicit hc: HeaderCarrier
  ): Future[ClaimToAdjustPoaResponse] = {

    val headers = appConfig.getIFHeaders("1773") ++ Seq(("Correlation-Id" -> randomUUID.toString))
    val updatedHc: HeaderCarrier = hc.withExtraHeaders(headers:_*)

    http
      .post(url"$endpoint")(updatedHc)
      .withBody(Json.toJson(request))
      .execute[ClaimToAdjustPoaResponse]
      .recover {
        case e =>
          Logger("application").error(e.getMessage)
          ClaimToAdjustPoaResponse(INTERNAL_SERVER_ERROR, Left(ErrorResponse(e.getMessage)))
      }
  }
}

