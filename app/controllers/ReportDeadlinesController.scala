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

package controllers

import javax.inject.{Inject, Singleton}

import controllers.predicates.AuthenticationPredicate
import models.reportDeadlines.{ReportDeadlinesErrorModel, ReportDeadlinesModel}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.ReportDeadlinesService
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ReportDeadlinesController @Inject()(val authentication: AuthenticationPredicate,
                                          val reportDeadlinesService: ReportDeadlinesService,
                                          cc: ControllerComponents

                                         ) extends BaseController(cc) {

  def getReportDeadlines(incomeSourceId: String, nino: String): Action[AnyContent] = authentication.async { implicit request =>
    Logger.debug(s"[ReportDeadlinesController][getReportDeadlines] - " +
      s"Requesting obligations from ReportDeadlinesService for incomeSourceId: $incomeSourceId, nino: $nino")
    reportDeadlinesService.getReportDeadlines(Some(incomeSourceId), nino).map {
      case success: ReportDeadlinesModel =>
        Logger.debug(s"[ReportDeadlinesController][getReportDeadlines] - Successful Response: $success")
        Ok(Json.toJson(success))
      case error: ReportDeadlinesErrorModel =>
        Logger.error(s"[ReportDeadlinesController][getReportDeadlines] - Error Response: $error")
        Status(error.status)(Json.toJson(error))
    }
  }

}
