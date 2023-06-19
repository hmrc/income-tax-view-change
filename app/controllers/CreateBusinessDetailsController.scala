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
import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.{CreateBusinessDetailsErrorResponse, CreateBusinessDetailsModel}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
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
    request.body.asJson match {
      case Some(body) =>
        createBusinessDetailsService.createBusinessDetails(mtdbsaRef, body) map {
          case success: CreateBusinessDetailsModel =>
            Ok(Json.toJson(success))
          case error: CreateBusinessDetailsErrorResponse =>
            logger.error(s"[CreateBusinessDetailsController][createBusinessDetails] - Error Response: $error")
            Status(error.status)(Json.toJson(error))
          case _ =>
            logger.error(s"[GetBusinessDetailsController][createBusinessDetails] - Unexpected Response")
            InternalServerError("Unexpected response received from CreateBusinessDetailsService")
        }
      case _ =>
        Future {
          BadRequest("[CreateBusinessDetailsController][getBusinessDetails]: Error - no payload found")
        }
    }
  }
}
