/*
 * Copyright 2018 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import config.MicroserviceAppConfig
import models.{IncomeSourceDetailsModel, IncomeSourcesError, IncomeSourcesResponseModel}
import play.api.http.Status
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import play.api.http.Status._
import scala.concurrent.Future
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import play.api.Logger


@Singleton
class IncomeSourceDetailsConnector @Inject()(val http: HttpClient,
                                             val appConfig: MicroserviceAppConfig
                                            ) extends RawResponseReads {

  val getIncomeSourceDetailsUrl: String => String = idNumber => s"registration/business-details/mtdbsa/$idNumber"

  def getIncomeSourceDetails(idNumber: String)(implicit headerCarrier: HeaderCarrier): Future[IncomeSourcesResponseModel] = {

    val url = getIncomeSourceDetailsUrl(idNumber)

    val desHC = headerCarrier.copy(authorization = Some(Authorization(s"Bearer ${appConfig.desToken}")))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)

    Logger.debug(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - Calling GET $url \n\nHeaders: $desHC")
    http.GET[HttpResponse](url)(httpReads, desHC, implicitly) map {
      response =>
        response.status match {
          case OK =>
            Logger.debug(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - RESPONSE status:${response.status}, body:${response.body}")
            response.json.validate[IncomeSourceDetailsModel].fold(
              invalid => {
                Logger.warn(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - Json ValidationError. Parsing IncomeSourceDetails")
                IncomeSourcesError(Status.INTERNAL_SERVER_ERROR,"Json Validation Error. Parsing IncomeSourceDetails")
              }, valid => valid
            )
          case _ =>
            Logger.debug(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - RESPONSE status: ${response.status}, body: ${response.body}")
            Logger.warn(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - Response status: [${response.status}] returned from getIncomeSourceDetails")
            IncomeSourcesError(response.status,response.body)
        }
    } recover {
      case _ =>
        Logger.warn(s"[IncomeSourceDetailsConnector][getIncomeSourceDetails] - Unexpected failed future")
        IncomeSourcesError(Status.INTERNAL_SERVER_ERROR,"Unexpected failed future")
    }
  }
}
