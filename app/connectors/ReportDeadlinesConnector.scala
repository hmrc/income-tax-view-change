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

  private[connectors] val getReportDeadlinesUrl: String => String =
    nino => s"${appConfig.desUrl}/enterprise/obligation-data/nino/$nino/ITSA?status=O"

  def getReportDeadlines(nino: String)(implicit headerCarrier: HeaderCarrier): Future[Either[ReportDeadlinesErrorModel, ObligationsModel]] = {

    val url = getReportDeadlinesUrl(nino)
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
                Logger.warn(s"[ReportDeadlinesConnector][getReportDeadlines] - Json ValidationError. Parsing Report Deadlines Data")
                Logger.debug(s"[ReportDeadlinesConnector][getReportDeadlines] - Json valiation error: $invalid")
                Left(ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Report Deadlines Data "))
              },
              valid => Right(valid)
            )
          case _ =>
            Logger.debug(s"[ReportDeadlinesConnector][getReportDeadlines] - RESPONSE status: ${response.status}, body: ${response.body}")
            Logger.warn(s"[ReportDeadlinesConnector][getReportDeadlines] - Response status: [${response.status}] returned from Report Deadlines call")
            Left(ReportDeadlinesErrorModel(response.status, response.body))
        }
    } recover {
      case ex =>
        Logger.warn(s"[ReportDeadlinesConnector][getReportDeadlines] - Unexpected failed future, ${ex.getMessage}")
        Left(ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}"))
      case _ =>
        Logger.warn(s"[ReportDeadlinesConnector][getReportDeadlines] - Unexpected failed future")
        Left(ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future"))
    }
  }
}