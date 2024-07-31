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

package controllers

import connectors.ObligationsConnector
import controllers.predicates.AuthenticationPredicate
import models.obligations.{ObligationsErrorModel, ObligationsModel, ObligationsResponseModel}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ObligationsController @Inject()(val authentication: AuthenticationPredicate,
                                      val obligationsConnector: ObligationsConnector,
                                      cc: ControllerComponents
                                     )(implicit ec: ExecutionContext) extends BackendController(cc) with Logging {
  private def handleObligationsResponse(response: ObligationsResponseModel): Result = {
    response match {
      case success: ObligationsModel =>
        logger.debug(s"Successful Response: $success")
        Ok(Json.toJson(success))
      case error: ObligationsErrorModel =>
        logger.error(s"Error Response: $error")
        Status(error.status)(Json.toJson(error))
    }
  }

  def getOpenObligations(nino: String): Action[AnyContent] = authentication.async { implicit request =>
    logger.debug("" +
      s"Requesting obligations for nino: $nino")
    obligationsConnector.getOpenObligations(nino).map(response =>
      handleObligationsResponse(response)
    )
  }

  def getAllObligations(nino: String, from: String, to: String): Action[AnyContent] = authentication.async { implicit request =>
    logger.debug("" +
      s"Requesting obligations for nino: $nino, from: $from, to: $to")
    obligationsConnector.getAllObligationsWithinDateRange(nino, from, to).map(response =>
      handleObligationsResponse(response)
    )
  }

}
