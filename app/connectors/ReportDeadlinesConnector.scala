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
import models.reportDeadlines.{ObligationsModel, ReportDeadlinesErrorModel, ReportDeadlinesResponseModel}
import play.api.http.Status
import play.api.http.Status._
import services.DateService
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReportDeadlinesConnector @Inject()(val http: HttpClient,
                                         val appConfig: MicroserviceAppConfig,
                                         val dateService: DateService
                                        )(implicit ec: ExecutionContext) extends RawResponseReads {

  private[connectors] def getReportDeadlinesUrl(nino: String, openObligations: Boolean): String = {
    val status: String = if (openObligations) "O" else "F"
    val toDate: LocalDate = dateService.getCurrentDate
    val fromDate: LocalDate = toDate.minusDays(365)
    val dateParameters: String = if (openObligations) "" else s"&from=$fromDate&to=$toDate"
    s"${appConfig.desUrl}/enterprise/obligation-data/nino/$nino/ITSA?status=$status$dateParameters"
  }

  private[connectors] def getAllReportDeadlinesUrl(nino: String, from: String, to: String): String = {
    s"${appConfig.desUrl}/enterprise/obligation-data/nino/$nino/ITSA?from=$from&to=$to"
  }

  def getReportDeadlines(nino: String, openObligations: Boolean)
                        (implicit headerCarrier: HeaderCarrier): Future[ReportDeadlinesResponseModel] = {
    val url = getReportDeadlinesUrl(nino, openObligations)

    logger.info(s"URL - $url ")
    logger.debug(s"Calling GET $url \n\nHeaders: $headerCarrier \nAuth Headers: ${appConfig.desAuthHeaders}")
    http.GET[HttpResponse](url = url, headers = appConfig.desAuthHeaders)(httpReads, headerCarrier, implicitly) map {
      response =>
        response.status match {
          case OK =>
            logger.info(s"RESPONSE status: ${response.status}, body: ${response.body}")
            response.json.validate[ObligationsModel](ObligationsModel.desReadsApi1330).fold(
              invalid => {
                logger.error(s"Json validation error: $invalid")
                ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Report Deadlines Data")
              },
              valid => {
                logger.info("successfully parsed response to ObligationsModel")
                valid
              }
            )
          case _ =>
            logger.error(s"RESPONSE status: ${response.status}, body: ${response.body}")
            ReportDeadlinesErrorModel(response.status, response.body)
        }
    } recover {
      case ex =>
        logger.error(s"Unexpected failed future, ${ex.getMessage}")
        ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}")
    }
  }

  def getAllObligations(nino: String, from: String, to: String)
                       (implicit headerCarrier: HeaderCarrier): Future[ReportDeadlinesResponseModel] = {
    val url = getAllReportDeadlinesUrl(nino, from, to)

    logger.info(s"Calling GET $url \n\nHeaders: $headerCarrier \nAuth Headers: ${appConfig.desAuthHeaders}")
    http.GET[HttpResponse](url = url, headers = appConfig.desAuthHeaders)(httpReads, headerCarrier, implicitly) map {
      response =>
        response.status match {
          case OK =>
            logger.info(s"RESPONSE status: ${response.status}, body: ${response.body}")
            response.json.validate[ObligationsModel](ObligationsModel.desReadsApi1330).fold(
              invalid => {
                logger.error(s"Json validation error: $invalid")
                ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing Report Deadlines Data")
              },
              valid => {
                logger.info("successfully parsed response to ObligationsModel")
                valid
              }
            )
          case _ =>
            logger.error(s"RESPONSE status: ${response.status}, body: ${response.body}")
            ReportDeadlinesErrorModel(response.status, response.body)
        }
    } recover {
      case ex =>
        logger.error(s"Unexpected failed future, ${ex.getMessage}")
        ReportDeadlinesErrorModel(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}")
    }
  }
}
