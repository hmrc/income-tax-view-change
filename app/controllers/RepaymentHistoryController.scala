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

import connectors.RepaymentHistoryDetailsConnector
import connectors.httpParsers.RepaymentHistoryHttpParser.UnexpectedRepaymentHistoryResponse
import controllers.predicates.AuthenticationPredicate
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class RepaymentHistoryController @Inject()(authentication: AuthenticationPredicate,
                                           cc: ControllerComponents,
                                           repaymentHistoryDetailsConnector: RepaymentHistoryDetailsConnector)
                                          (implicit ec: ExecutionContext) extends BackendController(cc) {


  def getRepaymentHistoryByDate(nino: String, fromDate: String, toDate: String): Action[AnyContent] =
    authentication.async { implicit request =>
      repaymentHistoryDetailsConnector.getRepaymentHistoryDetailsByDate(
        nino = nino,
        fromDate = fromDate,
        toDate = toDate
      ) map {
        case Right(repaymentHistory) => Ok(Json.toJson(repaymentHistory))
        case Left(error: UnexpectedRepaymentHistoryResponse) if error.code >= 400 && error.code < 500 => Status(error.code)(error.response)
        case Left(_) =>
          InternalServerError("Failed to retrieve repayment history by date range")
      }
    }


  def getRepaymentHistoryById(nino: String, repaymentId: String): Action[AnyContent] =
    authentication.async { implicit request =>
      repaymentHistoryDetailsConnector.getRepaymentHistoryDetailsById(
        nino = nino,
        repaymentId = repaymentId
      ) map {
        case Right(repaymentHistory) => Ok(Json.toJson(repaymentHistory))
        case Left(error: UnexpectedRepaymentHistoryResponse) if error.code >= 400 && error.code < 500 => Status(error.code)(error.response)
        case Left(_) =>
          InternalServerError("Failed to retrieve repayment history by ID")
      }
    }
}