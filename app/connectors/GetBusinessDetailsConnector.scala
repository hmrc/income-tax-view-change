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
import models.incomeSourceDetails.{Nino, BusinessDetailsAccessType, MtdId, IncomeSourceDetailsError, IncomeSourceDetailsModel, IncomeSourceDetailsNotFound, IncomeSourceDetailsResponseModel}
import play.api.http.Status
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetBusinessDetailsConnector @Inject()(val http: HttpClient,
                                            val appConfig: MicroserviceAppConfig
                                           )(implicit ec: ExecutionContext) extends RawResponseReads {

  def getUrl(accessType: BusinessDetailsAccessType, ninoOrMtdRef: String) = {
    accessType match {
      case Nino => s"${appConfig.ifUrl}/registration/business-details/nino/$ninoOrMtdRef"
      case MtdId => s"${appConfig.ifUrl}/registration/business-details/mtdId/$ninoOrMtdRef"
    }
  }

  def headers: Seq[(String, String)] = appConfig.getIFHeaders("1171")

  def getBusinessDetails(ninoOrMtdRef: String, accessType: BusinessDetailsAccessType)
                        (implicit headerCarrier: HeaderCarrier): Future[IncomeSourceDetailsResponseModel] = {

    val url = getUrl(accessType, ninoOrMtdRef)
    val jsonReads = IncomeSourceDetailsModel.ifReads

    logger.debug("[GetBusinessDetailsConnector][getBusinessDetails] - " +
      s"Calling GET $url \n\nHeaders: $headerCarrier \nAuth Headers: $headers")
    http.GET[HttpResponse](url = url, headers = headers)(httpReads, headerCarrier, implicitly) map {
      response =>
        response.status match {
          case OK =>
            logger.debug(s"[GetBusinessDetailsConnector][getBusinessDetails] - RESPONSE status:${response.status}, body:${response.body}")
            response.json.validate[IncomeSourceDetailsModel](jsonReads).fold(
              invalid => {
                logger.error(s"[GetBusinessDetailsConnector][getBusinessDetails] - Validation Errors: $invalid")
                IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Business Details")
              },
              valid => {
                logger.info("[GetBusinessDetailsConnector][getBusinessDetails] successfully parsed response to getBusinessDetails")
                valid
              }
            )
          case NOT_FOUND =>
            logger.warn(s"[GetBusinessDetailsConnector][getBusinessDetails] -  RESPONSE status: ${response.status}, body: ${response.body}")
            IncomeSourceDetailsNotFound(response.status, response.body)
          case _ =>
            logger.error(s"[GetBusinessDetailsConnector][getBusinessDetails] - RESPONSE status: ${response.status}, body: ${response.body}")
            IncomeSourceDetailsError(response.status, response.body)
        }
    } recover {
      case ex =>
        logger.error(s"[GetBusinessDetailsConnector][getBusinessDetails] - Unexpected failed future, ${ex.getMessage}")
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}")
    }
  }
}
