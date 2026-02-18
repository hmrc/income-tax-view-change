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

import config.MicroserviceAppConfig
import connectors.RepaymentHistoryDetailsConnector
import connectors.hip.HipRepaymentHistoryDetailsConnector
import connectors.httpParsers.RepaymentHistoryHttpParser.UnexpectedRepaymentHistoryResponse
import controllers.predicates.AuthenticationPredicate
import models.hip.GetRepaymentHistoryDetails
import play.api.libs.json.Json
import play.api.mvc.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RepaymentHistoryController @Inject()(authentication: AuthenticationPredicate,
                                           cc: ControllerComponents,
                                           repaymentHistoryDetailsConnector: RepaymentHistoryDetailsConnector,
                                           hipRepaymentHistoryDetailsConnector: HipRepaymentHistoryDetailsConnector,
                                           appConfig: MicroserviceAppConfig)
                                          (implicit ec: ExecutionContext) extends BackendController(cc) {


  def getAllRepaymentHistory(nino: String): Action[AnyContent] = {
    authentication.async { implicit request =>
      if (!appConfig.hipFeatureSwitchEnabled(GetRepaymentHistoryDetails)) {
        repaymentHistoryDetailsConnector.getAllRepaymentHistoryDetails(
          nino = nino
        ) map {
          case Right(repaymentHistory) => Ok(Json.toJson(repaymentHistory))
          case Left(error: UnexpectedRepaymentHistoryResponse) if error.code >= 400 && error.code < 500 => Status(error.code)(error.response)
          case Left(_) =>
            InternalServerError("Failed to retrieve repayment history by date range")
        }
      } else {
        hipRepaymentHistoryDetailsConnector.getRepaymentHistoryDetailsList(nino) map {
          case Right(repaymentHistory) => Ok(Json.toJson(repaymentHistory))
          case Left(error) =>
            if (error.status >= 400 && error.status < 500 ){
              Status(error.status)(Json.stringify(error.jsonError))
            }else {
              InternalServerError(Json.stringify(error.jsonError))
            }
        }
      }
    }
  }

  def getRepaymentHistoryById(nino: String, repaymentId: String): Action[AnyContent] =
    authentication.async { implicit request =>
      if (!appConfig.hipFeatureSwitchEnabled(GetRepaymentHistoryDetails)) {
        repaymentHistoryDetailsConnector.getRepaymentHistoryDetailsById(
          nino = nino,
          repaymentId = repaymentId
        ) map {
          case Right(repaymentHistory) => Ok(Json.toJson(repaymentHistory))
          case Left(error: UnexpectedRepaymentHistoryResponse) if error.code >= 400 && error.code < 500 => Status(error.code)(error.response)
          case Left(_) =>
            InternalServerError("Failed to retrieve repayment history by ID")
        }
      } else {
          hipRepaymentHistoryDetailsConnector.getRepaymentHistoryDetails(nino, repaymentId).map {
            case Right(repaymentHistory) => Ok(Json.toJson(repaymentHistory))
            case Left(error) =>
              if (error.status >= 400 && error.status < 500 ){
                Status(error.status)(Json.stringify(error.jsonError))
              }else {
                InternalServerError(Json.stringify(error.jsonError))
              }
          }
      }
    }
}