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
import models.{DesBusinessDetails, DesBusinessDetailsError, DesResponseModel}
import play.api.Logger
import play.api.http.Status
import play.api.http.Status._
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._


import scala.concurrent.Future

@Singleton
class NinoLookupConnector @Inject()(val http: HttpClient,
                                    val appConfig: MicroserviceAppConfig
                                   ) extends RawResponseReads {

  val getDesBusinessDetailsUrl: String => String =
    mtdRef => s"${appConfig.desUrl}/registration/business-details/mtdbsa/$mtdRef"

  def getDesBusinessDetails(mtdRef: String)(implicit headerCarrier: HeaderCarrier): Future[DesResponseModel] = {

    val url = getDesBusinessDetailsUrl(mtdRef)
    val desHC = headerCarrier.copy(authorization = Some(Authorization(s"Bearer ${appConfig.desToken}")))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)

    Logger.debug(s"[NinoLookupConnector][getDesBusinessDetails] - Calling GET $url \n\nHeaders: $desHC")
    http.GET[HttpResponse](url)(httpReads, desHC, implicitly) map {
      response =>
        response.status match {
          case OK =>
            Logger.debug(s"[NinoLookupConnector][getDesBusinessDetails] - RESPONSE status:${response.status}, body:${response.body}")
            response.json.validate[DesBusinessDetails] fold(
              invalid => {
                Logger.warn(s"[NinoLookupConnector][getDesBusinessDetails] - Json ValidationError. Parsing Des Business Details")
                DesBusinessDetailsError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Des Business Details")
              },
              valid => valid
            )
          case _ =>
            Logger.debug(s"[NinoLookupConnector][getDesBusinessDetails] - RESPONSE status: ${response.status}, body: ${response.body}")
            Logger.warn(s"[NinoLookupConnector][getDesBusinessDetails] - Response status: [${response.status}] returned from Des Business Details call")
            DesBusinessDetailsError(response.status, response.body)
        }
    } recover {
        case _ =>
          Logger.warn(s"[NinoLookupConnector][getDesBusinessDetails] - Unexpected failed future")
          DesBusinessDetailsError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future")

    }
  }
}
