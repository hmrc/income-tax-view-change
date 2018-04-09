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

package services

import javax.inject.{Inject, Singleton}

import connectors.ReportDeadlinesConnector
import models.reportDeadlines.{ReportDeadlinesErrorModel, ReportDeadlinesModel, ReportDeadlinesResponseModel}
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ReportDeadlinesService @Inject()(val reportDeadlinesConnector: ReportDeadlinesConnector){

  def getReportDeadlines(selfEmpId: String)(implicit headerCarrier: HeaderCarrier): Future[ReportDeadlinesResponseModel] = {

    Logger.debug("[ReportDeadlinesService][getReportDeadlines] - Requesting obligation data from Connector")

    reportDeadlinesConnector.getReportDeadlines(selfEmpId).map {
      case success: ReportDeadlinesModel =>
        Logger.debug(s"[ReportDeadlinesService][getReportDeadlines] - Retrieved obligation data:\n\n$success")
        success
      case error: ReportDeadlinesErrorModel =>
        Logger.debug(s"[ReportDeadlinesService][getReportDeadlines] - Received Error model from connector:\n\n$error")
        error
    }

  }

}
