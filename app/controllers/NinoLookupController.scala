/*
 * Copyright 2018 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import controllers.predicates.AuthenticationPredicate
import models.{IncomeSourceDetailsError, NinoModel}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services.NinoLookupService
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class NinoLookupController @Inject()(val authentication: AuthenticationPredicate,
                                     val ninoLookupService: NinoLookupService
                                      ) extends BaseController {

  def getNino(mtdRef: String): Action[AnyContent] = authentication.async { implicit request =>
    Logger.debug(s"[NinoLookupController][getNino] - Requesting NINO from NinoLookupService for MtdRef: $mtdRef")
    ninoLookupService.getNino(mtdRef).map {
      case success: NinoModel =>
        Logger.debug(s"[NinoLookupController][getNino] - Successful Response: $success")
        Ok(Json.toJson(success))
      case error: IncomeSourceDetailsError =>
        Logger.debug(s"[NinoLookupController][getNino] - Error Response: $error")
        Status(error.status)(Json.toJson(error))
    }
  }
}
