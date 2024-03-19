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

import connectors.ChargeHistoryDetailsConnector
import connectors.httpParsers.ChargeHistoryHttpParser.UnexpectedChargeHistoryResponse
import controllers.predicates.AuthenticationPredicate
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ChargeHistoryController @Inject()(authentication: AuthenticationPredicate,
                                        cc: ControllerComponents,
                                        chargeHistoryDetailsConnector: ChargeHistoryDetailsConnector)
                                       (implicit ec: ExecutionContext) extends BackendController(cc) {


  def getChargeHistoryDetails(mtdBsa: String, docNumber: String): Action[AnyContent] =
    authentication.async { implicit request =>
      chargeHistoryDetailsConnector.getChargeHistoryDetailsLegacy(
        mtdBsa = mtdBsa,
        docNumber = docNumber
      ) map {
        case Right(chargeHistory) => Ok(Json.toJson(chargeHistory))
        case Left(error: UnexpectedChargeHistoryResponse) if error.code >= 400 && error.code < 500 => Status(error.code)(error.response)
        case Left(_) =>
          InternalServerError("Failed to retrieve charges outstanding details")
      }
    }
}
