/*
 * Copyright 2025 HM Revenue & Customs
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

import config.MicroserviceAppConfig
import connectors.RawResponseReads
import connectors.hip.UpdateCustomerFactConnector.CorrelationIdHeader
import models.hip.UpdateCustomerFactHipApi
import models.hip.updateCustomerFact.{ErrorResponse, ErrorsResponse, UpdateCustomerFactRequest}
import play.api.Logging
import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, InternalServerError, Ok, UnprocessableEntity}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

object UpdateCustomerFactConnector {
  val CorrelationIdHeader = "correlationid"
}

class UpdateCustomerFactConnector @Inject()(val http: HttpClientV2,
                                    val appConfig: MicroserviceAppConfig
                                   )(implicit ec: ExecutionContext) extends RawResponseReads with Logging {

  val hipHeaders: Seq[(String, String)] = appConfig.getHIPHeaders(UpdateCustomerFactHipApi)

  private val updateCustomerFactsUrl: String = s"${appConfig.hipUrl}/etmp/RESTAdapter/customer/facts"

  def updateCustomerFactsToConfirmed(mtdsa: String)(implicit headerCarrier: HeaderCarrier): Future[Result] = {

    http.put(url"$updateCustomerFactsUrl")
      .withBody(Json.toJson[UpdateCustomerFactRequest](UpdateCustomerFactRequest(idValue = mtdsa)))
      .setHeader(hipHeaders: _*)
      .execute[HttpResponse]
      .map { response =>
        val correlationId = response.headers.get(CorrelationIdHeader).map(_.head).getOrElse(s"Unknown_$CorrelationIdHeader")
        response.status match {
          case OK =>
            logger.info("Customer fact successfully updated to Confirmed")
            Ok
          case BAD_REQUEST =>
            logger.error(s"Bad request while updating customer facts. CorrelationId: $correlationId, Reason: ${response.json.toString}")
            BadRequest
          case UNPROCESSABLE_ENTITY =>
            response.json.validate[ErrorResponse].orElse(response.json.validate[ErrorsResponse]).fold(
              invalid => {
                logger.error(s"Unexpected response with status code: $UNPROCESSABLE_ENTITY, response: $invalid, CorrelationId: $correlationId")
                UnprocessableEntity
              },
              {
                case expected@(_: ErrorResponse) =>
                  logger.error(s"Business Error Unprocessable. CorrelationId: $correlationId, " +
                    s"LogId:${expected.error.logID}, Code: ${expected.error.code}, Message: ${expected.error.message}")
                  UnprocessableEntity
                case expected@(_: ErrorsResponse) =>
                  logger.error(s"Business Error Unprocessable. CorrelationId: $correlationId, " +
                    s"Code: ${expected.errors.code}, Message: ${expected.errors.text}")
                  UnprocessableEntity
              }
            )
          case status =>
            logger.error(s"Unexpected response CorrelationId: $correlationId, status: $status, body: ${response.body}")
            InternalServerError
        }
      }
  }
}
