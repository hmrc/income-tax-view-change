/*
 * Copyright 2019 HM Revenue & Customs
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
import models.reportDeadlines.{ObligationsModel, ReportDeadlinesErrorModel, ReportDeadlinesModel, ReportDeadlinesResponseModel}
import play.api.Logger
import play.api.http.Status
import play.api.http.Status._
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.Future

@Singleton
class ReportDeadlinesConnector @Inject()(val http: HttpClient,
                                         val appConfig: MicroserviceAppConfig
                                        ) extends RawResponseReads {
  
  private[connectors] def getReportDeadlinesUrl(nino: String, openObligations: Boolean): String = {
    val status: String = if(openObligations) "O" else "F"
    s"${appConfig.desUrl}/enterprise/obligation-data/nino/$nino/ITSA?status=$status"
  }

  def getReportDeadlines(nino: String, openObligations: Boolean)
                        (implicit headerCarrier: HeaderCarrier): Future[Either[ReportDeadlinesErrorModel, ObligationsModel]] = {
    val url = getReportDeadlinesUrl(nino, openObligations)
    val desHC = headerCarrier.copy(authorization = Some(Authorization(s"Bearer ${appConfig.desToken}")))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)

    Logger.debug(s"[ReportDeadlinesConnector][getReportDeadlines] - Calling GET $url \n\nHeaders: $desHC")
    http.GET[HttpResponse](url)(httpReads, desHC, implicitly) map {
      response =>
        response.status match {
          case OK =>
            Logger.debug(s"[ReportDeadlinesConnector][getReportDeadlines] - RESPONSE status: ${response.status}, body: ${response.body}")
            response.json.validate[ObligationsModel](ObligationsModel.desReadsApi1330).fold(
              invalid => {
                Logger.error(s"[ReportDeadlinesConnector][getReportDeadlines] - Json valiation error: $invalid")
                Left(ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Report Deadlines Data "))
              },
              valid => {
                Logger.info(s"[ReportDeadlinesConnector][getReportDeadlines] successfully parsed response to ObligationsModel")
                Right(valid)
              }
            )
          case _ =>
            Logger.error(s"[ReportDeadlinesConnector][getReportDeadlines] - RESPONSE status: ${response.status}, body: ${response.body}")
            Left(ReportDeadlinesErrorModel(response.status, response.body))
        }
    } recover {
      case ex =>
        Logger.error(s"[ReportDeadlinesConnector][getReportDeadlines] - Unexpected failed future, ${ex.getMessage}")
        Left(ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}"))
    }
  }
}