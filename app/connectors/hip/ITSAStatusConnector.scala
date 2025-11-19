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
import connectors.itsastatus.ITSAStatusConnector.CorrelationIdHeader
import connectors.itsastatus.ITSAStatusConnectorTrait
import connectors.itsastatus.OptOutUpdateRequestModel.{OptOutUpdateRequest, OptOutUpdateResponse, OptOutUpdateResponseFailure, OptOutUpdateResponseSuccess}
import models.hip.ITSAStatusHipApi
import models.itsaStatus.{ITSAStatusResponse, ITSAStatusResponseError, ITSAStatusResponseModel, ITSAStatusResponseNotFound}
import play.api.Logging
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import play.api.libs.ws.writeableOf_JsValue

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

object ITSAStatusConnector {
  val CorrelationIdHeader = "CorrelationId"
}

class ITSAStatusConnector @Inject()(val http: HttpClientV2,
                                    val appConfig: MicroserviceAppConfig
                                   )(implicit ec: ExecutionContext) extends ITSAStatusConnectorTrait with RawResponseReads with Logging {

  val hipHeaders: Seq[(String, String)] = appConfig.getHIPHeaders(ITSAStatusHipApi)

  def getITSAStatusUrl(taxableEntityId: String, taxYear: String, futureYears: String, history: String): String =
    s"${appConfig.hipUrl}/itsd/person-itd/itsa-status/$taxableEntityId?taxYear=$taxYear&futureYears=$futureYears&history=$history"

  def updateItsaStatusUrl(taxableEntityId: String): String =
    s"${appConfig.hipUrl}/itsd/itsa-status/update/$taxableEntityId"

  def getITSAStatus(taxableEntityId: String, taxYear: String, futureYears: Boolean, history: Boolean)
                   (implicit headerCarrier: HeaderCarrier): Future[Either[ITSAStatusResponse, List[ITSAStatusResponseModel]]] = {

    val url = getITSAStatusUrl(taxableEntityId, taxYear, futureYears.toString, history.toString)


    logger.info("" +
      s"Calling GET $url \n\nHeaders: $headerCarrier \nAuth Headers: $hipHeaders")

    http.get(url"$url")
      .setHeader(
        hipHeaders: _*
      )
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case OK =>
            logger.debug(s"RESPONSE status:${response.status}, body:${response.body}")
            response.json.validate[List[ITSAStatusResponseModel]].fold(
              invalid => {
                logger.error(s"Validation Errors: $invalid")
                Left(ITSAStatusResponseError(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing ITSA Status Response"))
              },
              valid => {
                logger.info("successfully parsed response to getITSAStatus")
                Right(valid)
              }
            )
          case NOT_FOUND =>
            logger.warn(s" RESPONSE status: ${response.status}, body: ${response.body}")
            Left(ITSAStatusResponseNotFound(response.status, response.body))
          case _ =>
            logger.error(s"RESPONSE status: ${response.status}, body: ${response.body}, hc: ${headerCarrier}")
            Left(ITSAStatusResponseError(response.status, response.body))
        }
      } recover {
      case ex =>
        logger.error(s"Unexpected failed future, ${ex.getMessage}")
        Left(ITSAStatusResponseError(INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}"))
    }
  }

  def requestOptOutForTaxYear(taxableEntityId: String, optOutUpdateRequest: OptOutUpdateRequest)
                             (implicit headerCarrier: HeaderCarrier): Future[OptOutUpdateResponse] = {

    http.put(url"${updateItsaStatusUrl(taxableEntityId)}")
      .withBody(Json.toJson[OptOutUpdateRequest](optOutUpdateRequest))
      .setHeader(hipHeaders: _*)
      .execute[HttpResponse]
      .map{ response =>
        val correlationId = response.headers.get(CorrelationIdHeader).map(_.head).getOrElse(s"Unknown_$CorrelationIdHeader")
        response.status match {
          case NO_CONTENT =>
            logger.info("ITSA status successfully updated")
            OptOutUpdateResponseSuccess(correlationId)
          case _ =>
            response.json.validate[OptOutUpdateResponseFailure].fold(
              invalid => {
                val msg = s"Json validation error parsing itsa-status update response, error $invalid"
                logger.error(msg)
                OptOutUpdateResponseFailure.defaultFailure(msg, correlationId)
              },
              valid => {
                logger.debug(s"Unsuccessful response: $valid")
                valid.copy(correlationId = correlationId, statusCode = response.status)
              }
            )
        }
      }
  }
}
