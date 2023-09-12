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
import models.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel, IncomeSourceDetailsResponseModel}
import play.api.http.Status
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IncomeSourceDetailsConnector @Inject()(val http: HttpClient,
                                             val appConfig: MicroserviceAppConfig
                                            )(implicit ec: ExecutionContext) extends RawResponseReads {

  def getIncomeSourceDetailsUrl(mtdRef: String): String = {
    val platformUrl = if (appConfig.useBusinessDetailsIFPlatform) appConfig.ifUrl else appConfig.desUrl
    s"$platformUrl/registration/business-details/mtdbsa/$mtdRef"
  }

  def headers: Seq[(String, String)] = {
    if (appConfig.useBusinessDetailsIFPlatform) appConfig.ifAuthHeaders else appConfig.desAuthHeaders
  }

  def getIncomeSourceDetails(mtdRef: String)(implicit headerCarrier: HeaderCarrier): Future[IncomeSourceDetailsResponseModel] = {

    val url = getIncomeSourceDetailsUrl(mtdRef)

    logger.debug(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - " +
      s"Calling GET $url \n\nHeaders: $headerCarrier \nAuth Headers: ${appConfig.desAuthHeaders}")
    http.GET[HttpResponse](url = url, headers = headers)(httpReads, headerCarrier, implicitly) map {
      response =>
        response.status match {
          case OK =>
            logger.debug(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - RESPONSE status:${response.status}, body:${response.body}")
            response.json.validate[IncomeSourceDetailsModel](IncomeSourceDetailsModel.desReads) fold(
              invalid => {
                logger.error(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - Validation Errors: $invalid")
                IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Des Business Details")
              },
              valid => {
                logger.info(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] successfully parsed response to LastTaxCalculation")
                valid
              }
            )
          case _ =>
            logger.error(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - RESPONSE status: ${response.status}, body: ${response.body}")
            IncomeSourceDetailsError(response.status, response.body)
        }
    } recover {
      case ex =>
        logger.error(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - Unexpected failed future, ${ex.getMessage}")
        IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}")
    }
  }
}
