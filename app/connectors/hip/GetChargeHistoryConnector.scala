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
import models.hip.chargeHistory.{ChargeHistoryError, ChargeHistoryNotFound, ChargeHistoryResponseError, ChargeHistorySuccessWrapper}
import models.hip.{GetChargeHistoryHipApi, HipResponseErrorsObject}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK, UNPROCESSABLE_ENTITY}
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetChargeHistoryConnector @Inject()(val http: HttpClientV2,
                                          val appConfig: MicroserviceAppConfig) extends RawResponseReads with HipConnectorDataHelper {

  def getChargeHistoryDetailsUrl(idType: String, idValue: String, chargeReference: String): String = {
    s"${appConfig.hipUrl}/etmp/RESTAdapter/ITSA/TaxPayer/GetChargeHistory?idType=$idType&idValue=$idValue&chargeReference=$chargeReference"
  }

  def getHeaders: Seq[(String, String)] = appConfig.getHIPHeaders(GetChargeHistoryHipApi, Some(xMessageTypeFor5705))

  def getChargeHistory(idValue: String, chargeReference: String)
                      (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[ChargeHistoryResponseError, ChargeHistorySuccessWrapper]] = {

    val url = getChargeHistoryDetailsUrl("NINO", idValue, chargeReference)

    http
      .get(url"$url")
      .setHeader(getHeaders: _*)
      .execute[HttpResponse]
      .map {
        response =>
          response.status match {
            case OK =>
              logger.debug(s"RESPONSE status:${response.status}, body:${response.body}")
              response.json.validate[ChargeHistorySuccessWrapper].fold(
                invalid => {
                  logger.error(s"Validation Errors: $invalid")
                  Left(ChargeHistoryError(INTERNAL_SERVER_ERROR, "Json validation error parsing ChargeHistorySuccess model"))
                }, {
                  valid =>
                  logger.info("Successfully parsed response to ChargeHistorySuccess model")
                  Right(valid)
                }
              )
            case NOT_FOUND =>
              logger.warn(s" RESPONSE status: ${response.status}, body: ${response.body}")
              Left(ChargeHistoryNotFound(response.status, response.body))
            case UNPROCESSABLE_ENTITY => Left(handleUnprocessableStatusResponse(response))
            case _ =>
              logger.error(s"RESPONSE status: ${response.status}, body: ${response.body}")
              Left(ChargeHistoryError(response.status, response.body))
          }
      } recover {
      case ex =>
        logger.error(s"Unexpected failed future, ${ex.getMessage}")
        Left(ChargeHistoryError(INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}"))
    }
  }

  private def handleUnprocessableStatusResponse(unprocessableResponse: HttpResponse): ChargeHistoryResponseError = {
    val notFoundCodes = Set("005", "014")
    unprocessableResponse.json.validate[HipResponseErrorsObject] match {
      case JsError(errors) =>
        logger.error("Unable to parse response as Business Validation Error - " + errors)
        logger.error(s"${unprocessableResponse.status} returned from HiP with body: ${unprocessableResponse.body}")
        ChargeHistoryError(unprocessableResponse.status, unprocessableResponse.body)
      case JsSuccess(success, _) =>
        success match {
          case error: HipResponseErrorsObject if notFoundCodes.contains(error.errors.code) =>
            logger.info(s"Resource not found code identified, code:${error.errors.code}, converting to 404 response")
            ChargeHistoryNotFound(NOT_FOUND, unprocessableResponse.body)
          case _ =>
            logger.error(s"${unprocessableResponse.status} returned from HiP with body: ${unprocessableResponse.body}")
            ChargeHistoryError(unprocessableResponse.status, unprocessableResponse.body)
        }
    }
  }
}
