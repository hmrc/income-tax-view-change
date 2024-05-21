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

import connectors.itsastatus.ITSAStatusConnector
import controllers.predicates.AuthenticationPredicate
import models.itsaStatus.{ITSAStatusResponseError, ITSAStatusResponseNotFound}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ITSAStatusController @Inject()(authentication: AuthenticationPredicate,
                                     cc: ControllerComponents,
                                     connector: ITSAStatusConnector)
                                    (implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def getITSAStatus(taxableEntityId: String, taxYear: String, futureYears: Boolean,
                    history: Boolean): Action[AnyContent] = authentication.async { implicit request =>
    connector.getITSAStatus(
      taxableEntityId = taxableEntityId,
      taxYear = taxYear,
      futureYears = futureYears, history = history).map {
      case Left(error: ITSAStatusResponseNotFound) =>
        logger.warn(s"[ITSAStatusController][getITSAStatus] - ITSA Status not found: $error")
        Status(error.status)(Json.toJson(error))
      case Left(error: ITSAStatusResponseError) =>
        logger.error(s"[ITSAStatusController][getITSAStatus] - Error Response: $error")
        Status(error.status)(Json.toJson(error))
      case Left(error) =>
        logger.error(s"[ITSAStatusController][getITSAStatus][ITSAStatusResponseModel] - Error fetching ITSA Status: $error")
        InternalServerError("[ITSAStatusController][getITSAStatus]")
      case Right(result) =>
        logger.debug(s"[ITSAStatusController][getITSAStatus] - Successful Response: $result")
        Ok(Json.toJson(result))
    }
  }

}
