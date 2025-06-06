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

import config.MicroserviceAppConfig
import connectors.RawResponseReads
import models.hip.{GetBusinessDetailsHipApi, HipResponseErrorsObject}
import models.hip.incomeSourceDetails._
import play.api.http.Status
import play.api.http.Status.{NOT_FOUND, OK, UNPROCESSABLE_ENTITY}
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetBusinessDetailsConnector @Inject()(val http: HttpClientV2,
                                            val appConfig: MicroserviceAppConfig) extends RawResponseReads with HipConnectorDataHelper {

  private val isGetBusinessDetailsEnabledInHip = appConfig.hipFeatureSwitchEnabled(GetBusinessDetailsHipApi)

  def getBusinessDetailsUrl(accessType: BusinessDetailsAccessType, ninoOrMtdRef: String): String = {
    accessType match {
      case Nino => s"${appConfig.hipUrl}/etmp/RESTAdapter/itsa/taxpayer/business-details?nino=$ninoOrMtdRef"
      case MtdId => s"${appConfig.hipUrl}/etmp/RESTAdapter/itsa/taxpayer/business-details?mtdReference=$ninoOrMtdRef"
    }
  }

  def getHeaders: Seq[(String, String)] = appConfig.getHIPHeaders(GetBusinessDetailsHipApi, Some(xMessageTypeFor5266))

  def getBusinessDetails(ninoOrMtdRef: String, accessType: BusinessDetailsAccessType)
                        (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[IncomeSourceDetailsResponseModel] = {
    val url = getBusinessDetailsUrl(accessType, ninoOrMtdRef)

    logger.debug(
      s"Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: $getHeaders" +
        s" \nIs1171GetBusinessDetailsEnabledInHip: ${if (isGetBusinessDetailsEnabledInHip) "YES" else "NO"}"
    )

    http
      .get(url"$url")
      .setHeader(
        getHeaders: _*
      )
      .execute[HttpResponse]
      .map {
        response =>
          response.status match {
            case OK =>
              logger.debug(s"RESPONSE status:${response.status}, body:${response.body}")
              response.json.validate[IncomeSourceDetailsModel].fold(
                invalid => {
                  logger.error(s"Validation Errors: $invalid")
                  IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Business Details")
                },
                valid => {
                  logger.info("successfully parsed response to getBusinessDetails")
                  valid
                }
              )
            case NOT_FOUND =>
              logger.warn(s" RESPONSE status: ${response.status}, body: ${response.body}")
              IncomeSourceDetailsNotFound(response.status, response.body)
            case UNPROCESSABLE_ENTITY => handleUnprocessableStatusResponse(response)
            case _ =>
              logger.error(s"RESPONSE status: ${response.status}, body: ${response.body}")
              IncomeSourceDetailsError(response.status, response.body)
          }
      } recover {
      case ex =>
        logger.error(s"Unexpected failed future, ${ex.getMessage}")
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}")
    }
  }

  private def handleUnprocessableStatusResponse(unprocessableResponse: HttpResponse): IncomeSourceDetailsResponseModel = {
    unprocessableResponse.json.validate[HipResponseErrorsObject] match {
      case JsError(errors) =>
        logger.error("Unable to parse response as Business Validation Error - " + errors)
        logger.error(s"${unprocessableResponse.status} returned from HiP with body: ${unprocessableResponse.body}")
        IncomeSourceDetailsError(unprocessableResponse.status, unprocessableResponse.body)
      case JsSuccess(success, _) =>
        success match {
          case error: HipResponseErrorsObject if error.errors.code == "006" || error.errors.code == "008" =>
            logger.info(s"Resource not found code identified, converting to 404 response")
            IncomeSourceDetailsNotFound(NOT_FOUND, unprocessableResponse.body)
          case _ =>
            logger.error(s"${unprocessableResponse.status} returned from HiP with body: ${unprocessableResponse.body}")
            IncomeSourceDetailsError(unprocessableResponse.status, unprocessableResponse.body)
        }
    }
  }
}
