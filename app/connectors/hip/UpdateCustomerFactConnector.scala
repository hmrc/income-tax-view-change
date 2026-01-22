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
import models.hip.UpdateCustomerFactHipApi
import models.hip.updateCustomerFact.{ErrorResponse, ErrorsResponse, UpdateCustomerFactRequest, UpdateCustomerFactResponse}
import play.api.Logging
import play.api.http.Status.*
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.writeableOf_JsValue
import play.api.mvc.Result
import play.api.mvc.Results.{Ok, Status, UnprocessableEntity}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class UpdateCustomerFactConnector @Inject()(
                                             val http: HttpClientV2,
                                             val appConfig: MicroserviceAppConfig
                                           )(implicit ec: ExecutionContext) extends RawResponseReads with Logging {

  private val CorrelationIdHeader = "correlationid"

  private val hipHeaders: Seq[(String, String)] = appConfig.getHIPHeaders(UpdateCustomerFactHipApi)
  private val updateCustomerFactsUrl: String = s"${appConfig.hipUrl}/etmp/RESTAdapter/customer/facts"

  private def responseJsonOpt(response: HttpResponse): Option[JsValue] =
    Try(Json.parse(response.body)).toOption

  private def resultWithBody(status: Int, response: HttpResponse): Result =
    responseJsonOpt(response)
      .map(json => Status(status)(json))
      .getOrElse(Status(status)(response.body))

  def updateCustomerFactsToConfirmed(mtdsa: String)(implicit headerCarrier: HeaderCarrier): Future[Result] = {

    val request = UpdateCustomerFactRequest.confirmedZorigin(mtdsa)

    http.put(url"$updateCustomerFactsUrl")
      .withBody(Json.toJson(request))
      .setHeader(hipHeaders: _*)
      .execute[HttpResponse]
      .map { response =>
        val correlationId = response.headers.get(CorrelationIdHeader).flatMap(_.headOption).getOrElse(s"Unknown_$CorrelationIdHeader")

        response.status match {
          case OK =>
            response.json.validate[UpdateCustomerFactResponse].fold(
              invalid => {
                logger.warn(
                  s"Customer fact updated (200) but response didn't match expected schema. " +
                    s"CorrelationId: $correlationId, errors: $invalid, body: ${response.json}"
                )
                Ok(response.json)
              },
              _ => {
                logger.info(s"Customer fact successfully updated to Confirmed. CorrelationId: $correlationId")
                Ok(response.json)
              }
            )
          case BAD_REQUEST =>
            logger.error(s"Bad request while updating customer facts. CorrelationId: $correlationId, body: ${response.body}")
            resultWithBody(BAD_REQUEST, response)
          case UNPROCESSABLE_ENTITY =>
            val payload: JsValue = responseJsonOpt(response).getOrElse(Json.obj("raw" -> response.body))
            payload.validate[ErrorResponse].fold(
              _ => payload.validate[ErrorsResponse].fold(
                invalid => {
                  logger.error(s"Business Error Unprocessable but payload didn't match expected schemas." +
                    s"CorrelationId: $correlationId, errors: $invalid, body: $payload")
                  UnprocessableEntity(payload)
                },
                ers => {
                  logger.error(s"Business Error Unprocessable." +
                    s"CorrelationId: $correlationId, Code: ${ers.errors.code}, Message: ${ers.errors.text}")
                  UnprocessableEntity(payload)
                }
              ),
              er => {
                logger.error(s"Business Error Unprocessable." +
                  s"CorrelationId: $correlationId, LogId: ${er.error.logID}, Code: ${er.error.code}, Message: ${er.error.message}")
                UnprocessableEntity(payload)
              }
            )
          case INTERNAL_SERVER_ERROR | SERVICE_UNAVAILABLE =>
            logger.error(s"HIP error while updating customer facts." +
              s"CorrelationId: $correlationId, status: ${response.status}, body: ${response.body}")
            resultWithBody(response.status, response)
          case status =>
            logger.error(s"Unexpected response CorrelationId: $correlationId, status: $status, body: ${response.body}")
            resultWithBody(status, response)
        }
      }
  }
}
