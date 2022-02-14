/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.ReportDeadlinesConnector
import controllers.predicates.AuthenticationPredicate
import models.reportDeadlines.{ObligationsModel, ReportDeadlinesErrorModel}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ReportDeadlinesController @Inject()(val authentication: AuthenticationPredicate,
                                          val reportDeadlinesConnector: ReportDeadlinesConnector,
                                          cc: ControllerComponents
                                         ) extends BackendController(cc) with Logging {

  def getOpenObligations(nino: String): Action[AnyContent] = authentication.async { implicit request =>
    logger.debug(s"[ReportDeadlinesController][getOpenObligations] - " +
      s"Requesting obligations from ReportDeadlinesService for nino: $nino")
    reportDeadlinesConnector.getReportDeadlines(nino, openObligations = true).map {
      case success: ObligationsModel =>
        logger.debug(s"[ReportDeadlinesController][getOpenObligations] - Successful Response: $success")
        Ok(Json.toJson(success))
      case error: ReportDeadlinesErrorModel =>
        logger.error(s"[ReportDeadlinesController][getOpenObligations] - Error Response: $error")
        Status(error.status)(Json.toJson(error))
    }
  }

  def getFulfilledObligations(nino: String): Action[AnyContent] = authentication.async { implicit request =>
    logger.debug(s"[ReportDeadlinesController][getFulfilledObligations] - " +
      s"Requesting obligations from ReportDeadlinesService for nino: $nino")
    reportDeadlinesConnector.getReportDeadlines(nino, openObligations = false).map {
      case success: ObligationsModel =>
        logger.debug(s"[ReportDeadlinesController][getFulfilledObligations] - Successful Response: $success")
        Ok(Json.toJson(success))
      case error: ReportDeadlinesErrorModel =>
        logger.error(s"[ReportDeadlinesController][getFulfilledObligations] - Error Response: $error")
        Status(error.status)(Json.toJson(error))
    }
  }

  def getPreviousObligations(nino: String, from: String, to: String): Action[AnyContent] = authentication.async { implicit request =>
    logger.debug(s"[ReportDeadlinesController][getFulfilledObligations] - " +
      s"Requesting obligations from ReportDeadlinesService for nino: $nino, from: $from, to: $to")
    reportDeadlinesConnector.getPreviousObligations(nino, from, to).map {
      case success: ObligationsModel =>
        logger.debug(s"[ReportDeadlinesController][getFulfilledObligations] - Successful Response: $success")
        Ok(Json.toJson(success))
      case error: ReportDeadlinesErrorModel =>
        logger.error(s"[ReportDeadlinesController][getFulfilledObligations] - Error Response: $error")
        Status(error.status)(Json.toJson(error))
    }
  }

}
