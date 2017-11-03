/*
 * Copyright 2017 HM Revenue & Customs
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

import config.AppConfig
import controllers.predicates.AuthenticationPredicate
import models.{LastTaxCalculation, LastTaxCalculationError}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services.EstimatedTaxLiabilityService
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class EstimatedTaxLiabilityController @Inject()(implicit val appConfig: AppConfig,
                                                val authentication: AuthenticationPredicate,
                                                val estimatedTaxLiabilityService: EstimatedTaxLiabilityService
                                      ) extends BaseController {

  def getEstimatedTaxLiability(nino: String, year: String, calcType: String): Action[AnyContent] = authentication.async { implicit request =>
    Logger.debug(s"[EstimatedTaxLiabilityController][getEstimatedTaxLiability] - Requesting Estimate from EstimatedTaxLiabilityService for NINO: $nino")
    estimatedTaxLiabilityService.getEstimatedTaxLiability(nino, year, calcType).map {
      case success: LastTaxCalculation =>
        Logger.debug(s"[EstimatedTaxLiabilityController][getEstimatedTaxLiability] - Successful Response: $success")
        Ok(Json.toJson(success))
      case error: LastTaxCalculationError =>
        Logger.debug(s"[EstimatedTaxLiabilityController][getEstimatedTaxLiability] - Error Response: $error")
        Status(error.status)(Json.toJson(error))
    }
  }
}
