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

import connectors.ClaimToAdjustPoaConnector
import controllers.predicates.AuthenticationPredicate
import models.claimToAdjustPoa.ClaimToAdjustPoaApiResponse._
import models.claimToAdjustPoa.ClaimToAdjustPoaResponse.ErrorResponse
import models.claimToAdjustPoa.ClaimToAdjustPoaRequest
import play.api.{Logger, Logging}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ClaimToAdjustPoaController @Inject()(authentication: AuthenticationPredicate,
                                           cc: ControllerComponents,
                                           connector: ClaimToAdjustPoaConnector)
                                          (implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def submitClaimToAdjustPoa(): Action[AnyContent] = authentication.async {
    implicit request: Request[AnyContent] =>
      withValidRequest { claimToAdjustRequest =>
        connector.postClaimToAdjustPoa(claimToAdjustRequest).map {
          response =>
            response.claimToAdjustPoaBody match {
            case Right(x: SuccessResponse)  => Created(Json.toJson(x))
            case Left(e: ErrorResponse)     =>
              Logger("application").error(e.message)
              Status(response.status)(Json.toJson(e))
          }
        }
      }
    }

  private def withValidRequest (body: Function[ClaimToAdjustPoaRequest, Future[Result]])
                               (implicit request: Request[AnyContent]): Future[Result] = {
    val result = for {
      json <- request.body.asJson
      claimToAdjustRequest <- json.validate[ClaimToAdjustPoaRequest].asOpt
    } yield {
      body(claimToAdjustRequest)
    }
    result.getOrElse(Future.successful(BadRequest(Json.obj("message" -> "Could not validate request"))))
  }
}
