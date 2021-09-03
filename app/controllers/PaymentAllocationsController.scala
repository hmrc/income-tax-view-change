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

import connectors.PaymentAllocationsConnector
import connectors.httpParsers.PaymentAllocationsHttpParser.NotFoundResponse
import controllers.predicates.AuthenticationPredicate
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class PaymentAllocationsController @Inject()(authentication: AuthenticationPredicate,
                                             cc: ControllerComponents,
                                             paymentAllocationsConnector: PaymentAllocationsConnector)
                                            (implicit ec: ExecutionContext) extends BackendController(cc) {

  def getPaymentAllocations(nino: String, paymentLot: String, paymentLotItem: String): Action[AnyContent] = {
    authentication.async { implicit request =>
      paymentAllocationsConnector.getPaymentAllocations(
        nino = nino,
        paymentLot = paymentLot,
        paymentLotItem = paymentLotItem
      ) map {
        case Right(paymentAllocations) => Ok(Json.toJson(paymentAllocations))
        case Left(NotFoundResponse) => NotFound("No payment allocations found")
        case Left(_) => InternalServerError("Failed to retrieve payment allocations")
      }
    }
  }

}
