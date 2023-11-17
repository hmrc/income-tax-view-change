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

import controllers.predicates.AuthenticationPredicate
import models.incomeSourceDetails.CreateIncomeSourceRequest
import play.api.libs.json.Json
import play.api.mvc._
import play.api.{Logger, Logging}
import services.CreateBusinessDetailsService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


@Singleton
class CreateBusinessDetailsController @Inject()(val authentication: AuthenticationPredicate,
                                                val createBusinessDetailsService: CreateBusinessDetailsService,
                                                cc: ControllerComponents
                                               ) extends BackendController(cc) with Logging {

  def createBusinessDetails(mtdbsaRef: String): Action[AnyContent] = authentication.async { implicit request =>
    request.body
      .asJson.getOrElse(Json.obj()).validate[CreateIncomeSourceRequest].fold(
        invalidRequest => {
          Logger("application").error(s"[CreateBusinessDetailsController][createBusinessDetails] - invalid request: $invalidRequest")
          Future {
            BadRequest("[CreateBusinessDetailsController][createBusinessDetails]: Error - no payload found")
          }
        }, validRequest => {
          Logger("application").info(s"[CreateBusinessDetailsController][createBusinessDetails] - creating business request: $validRequest")
          createBusinessDetailsService.createBusinessDetails(mtdbsaRef, validRequest) map {
            case Right(successResponse) =>
              Ok(Json.toJson(successResponse))
            case Left(errorResponse) =>
              Logger("application").error(s"[CreateBusinessDetailsController][createBusinessDetails] - Error Response: $errorResponse")
              Status(errorResponse.status)(Json.toJson(errorResponse))
          }
        }
      )
  }
}
