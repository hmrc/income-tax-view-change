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

import connectors.FinancialDetailsConnector
import connectors.httpParsers.ChargeHttpParser.UnexpectedChargeResponse
import controllers.predicates.AuthenticationPredicate
import javax.inject.{Inject, Singleton}
import models.financialDetails.{Charge, Payment}
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class FinancialDetailPaymentsController @Inject()(authentication: AuthenticationPredicate,
                                                  cc: ControllerComponents,
                                                  financialDetailsConnector: FinancialDetailsConnector)
                                                 (implicit ec: ExecutionContext) extends BackendController(cc){

  def getPaymentDetails(nino: String, from: String, to: String): Action[AnyContent] = {
    authentication.async { implicit request =>
      financialDetailsConnector.listCharges(
        nino = nino,
        from = from,
        to = to
      ) map {
        case Right(chargesResponse) => Ok(Json.toJson(chargesResponse.financialDetails.flatMap(_.payments)))
        case Left(error: UnexpectedChargeResponse) if error.code >= 400 && error.code < 500 => Status(error.code)(error.response)
        case Left(_) => InternalServerError("Failed to retrieve charge details")

      }
    }
  }
}
