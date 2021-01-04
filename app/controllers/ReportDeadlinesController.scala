/*
 * Copyright 2021 HM Revenue & Customs
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

import controllers.predicates.AuthenticationPredicate
import javax.inject.{Inject, Singleton}
import models.reportDeadlines.{ObligationsModel, ReportDeadlinesErrorModel}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.ReportDeadlinesService
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ReportDeadlinesController @Inject()(val authentication: AuthenticationPredicate,
                                          val reportDeadlinesService: ReportDeadlinesService,
                                          cc: ControllerComponents
                                         ) extends BackendController(cc) {

  def getOpenObligations(nino: String): Action[AnyContent] = authentication.async { implicit request =>
    Logger.debug(s"[ReportDeadlinesController][getOpenObligations] - " +
      s"Requesting obligations from ReportDeadlinesService for nino: $nino")
    reportDeadlinesService.getReportDeadlines(nino, openObligations = true).map {
      case success: ObligationsModel =>
        Logger.debug(s"[ReportDeadlinesController][getOpenObligations] - Successful Response: $success")
        Ok(Json.toJson(success))
      case error: ReportDeadlinesErrorModel =>
        Logger.error(s"[ReportDeadlinesController][getOpenObligations] - Error Response: $error")
        Status(error.status)(Json.toJson(error))
    }
  }

  def getFulfilledObligations(nino: String): Action[AnyContent] = authentication.async { implicit request =>
    Logger.debug(s"[ReportDeadlinesController][getFulfilledObligations] - " +
      s"Requesting obligations from ReportDeadlinesService for nino: $nino")
    reportDeadlinesService.getReportDeadlines(nino, openObligations = false).map {
      case success: ObligationsModel =>
        Logger.debug(s"[ReportDeadlinesController][getFulfilledObligations] - Successful Response: $success")
        Ok(Json.toJson(success))
      case error: ReportDeadlinesErrorModel =>
        Logger.error(s"[ReportDeadlinesController][getFulfilledObligations] - Error Response: $error")
        Status(error.status)(Json.toJson(error))
    }
  }

}
