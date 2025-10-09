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

import connectors.hip.GetChargeHistoryConnector
import controllers.predicates.AuthenticationPredicate
import models.hip.chargeHistory.{ChargeHistoryError, ChargeHistoryNotFound, ChargeHistorySuccessWrapper}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ChargeHistoryController @Inject()(authentication: AuthenticationPredicate,
                                        cc: ControllerComponents,
                                        chargeHistoryDetailsConnector: GetChargeHistoryConnector)
                                       (implicit ec: ExecutionContext) extends BackendController(cc) {


  def getChargeHistoryDetails(nino: String, chargeReference: String): Action[AnyContent] =
    authentication.async { implicit request =>
      chargeHistoryDetailsConnector.getChargeHistory(
        idValue = nino,
        chargeReference = chargeReference
      ) map {
        case Right(chargeHistory) =>
          val convertToChargeHistoryModel = ChargeHistorySuccessWrapper.toChargeHistoryModel(chargeHistory)
          Ok(Json.toJson(convertToChargeHistoryModel))
        case Left(notFoundError: ChargeHistoryNotFound) =>
          Status(notFoundError.status)(Json.toJson(notFoundError))
        case Left(error: ChargeHistoryError) =>
          Status(error.status)(Json.toJson(error))
        case _ =>
          InternalServerError("Failed to retrieve charges outstanding details")
      }
    }
}
