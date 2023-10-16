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

package connectors

import config.MicroserviceAppConfig
import models.itsaStatus._
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ITSAStatusConnector @Inject()(val http: HttpClient,
                                    val appConfig: MicroserviceAppConfig
                                   )(implicit ec: ExecutionContext) extends RawResponseReads {

  def getITSAStatusUrl(taxableEntityId: String, taxYear: String) = s"${appConfig.ifUrl}/income-tax/$taxableEntityId/person-itd/itsa-status/$taxYear"

  def getITSAStatus(taxableEntityId: String, taxYear: String, futureYears: Boolean, history: Boolean)
                   (implicit headerCarrier: HeaderCarrier): Future[Either[ITSAStatusResponse, List[ITSAStatusResponseModel]]] = {

    val url = getITSAStatusUrl(taxableEntityId, taxYear)

    logger.info(s"[ITSAStatusConnector][getITSAStatus] - " +
      s"Calling GET $url \n\nHeaders: $headerCarrier \nAuth Headers: ${appConfig.ifAuthHeaders}")

    val queryParams: Seq[(String, String)] = Seq(("futureYears", futureYears.toString), ("history", history.toString))

    http.GET[HttpResponse](url = url, queryParams = queryParams, headers = appConfig.ifAuthHeaders)(httpReads, headerCarrier, implicitly) map {
      response =>
        response.status match {
          case OK =>
            logger.debug(s"[ITSAStatusConnector][getITSAStatus] - RESPONSE status:${response.status}, body:${response.body}")
            response.json.validate[List[ITSAStatusResponseModel]] fold(
              invalid => {
                logger.error(s"[ITSAStatusConnector][getITSAStatus] - Validation Errors: $invalid")
                Left(ITSAStatusResponseError(INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing ITSA Status Response"))
              },
              valid => {
                logger.info(s"[ITSAStatusConnector][getITSAStatus] successfully parsed response to getITSAStatus")
                Right(valid)
              }
            )
          case NOT_FOUND =>
            logger.warn(s"[ITSAStatusConnector][getITSAStatus] -  RESPONSE status: ${response.status}, body: ${response.body}")
            Left(ITSAStatusResponseNotFound(response.status, response.body))
          case _ =>
            logger.error(s"[ITSAStatusConnector][getITSAStatus] - RESPONSE status: ${response.status}, body: ${response.body}, hc: ${headerCarrier}")
            Left(ITSAStatusResponseError(response.status, response.body))
        }
    } recover {
      case ex =>
        logger.error(s"[ITSAStatusConnector][getITSAStatus] - Unexpected failed future, ${ex.getMessage}")
        Left(ITSAStatusResponseError(INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}"))
    }
  }
}
