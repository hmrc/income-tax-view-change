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
import models.incomeSourceDetails._
import play.api.http.Status
import play.api.http.Status._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BusinessDetailsConnector @Inject()(val http: HttpClientV2,
                                         val appConfig: MicroserviceAppConfig
                                        )(implicit ec: ExecutionContext) extends RawResponseReads {

  def getUrl(accessType: BusinessDetailsAccessType, ninoOrMtdRef: String): String = {
    accessType match {
      case Nino => s"${appConfig.ifUrl}/registration/business-details/nino/$ninoOrMtdRef"
      case MtdId => s"${appConfig.ifUrl}/registration/business-details/mtdId/$ninoOrMtdRef"
    }
  }

  def headers: Seq[(String, String)] = appConfig.getIFHeaders("1171")

  def getBusinessDetails(ninoOrMtdRef: String, accessType: BusinessDetailsAccessType)
                        (implicit headerCarrier: HeaderCarrier): Future[IncomeSourceDetailsResponseModel] = {

    val url = getUrl(accessType, ninoOrMtdRef)

    logger.debug("" +
      s"Calling GET $url \n\nHeaders: $headerCarrier \nAuth Headers: $headers")
    http
      .get(url"$url")
      .setHeader(headers: _*)
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
}
