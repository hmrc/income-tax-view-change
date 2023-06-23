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
import models.PreviousCalculation._
import models.errors
import models.errors.{InvalidNino, MultiError}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import services.PreviousCalculationService
import utils.urlUtils.isInvalidNino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PreviousCalculationController @Inject()(val authentication: AuthenticationPredicate,
                                              val calculationService: PreviousCalculationService,
                                              cc: ControllerComponents
                                             ) extends BackendController(cc) with Logging {

  def getPreviousCalculation(nino: String, year: String): Action[AnyContent] =
    authentication.async {
      implicit request =>
        if (isInvalidNino(nino)) {
          logger.error(s"[PreviousCalculationController][getPreviousCalculation] Invalid Nino '$nino' received in request.")
          Future.successful(BadRequest(Json.toJson[errors.Error](InvalidNino)))
        } else {
          getPreviousCalculation(nino, year)
        }
    }

  private def getPreviousCalculation(nino: String, year: String)(implicit hc: HeaderCarrier) = {
    logger.debug(s"[PreviousCalculationController][getPreviousCalculation] Calling CalculationService.getPreviousCalculation")
    calculationService.getPreviousCalculation(nino, year).map {
      case _@Right(previousCalculation) => Ok(Json.toJson(previousCalculation))
      case _@Left(error) => error.error match {
        case singleError: errors.Error =>
          logger.error(s"[PreviousCalculationController][getPreviousCalculation] returned a single error ${singleError.reason}")
          Status(error.status)(Json.toJson(singleError))
        case multiError: MultiError =>
          multiError.failures.foreach(singleError =>
            logger.error(s"[PreviousCalculationController][getPreviousCalculation] returned multiple errors ${singleError.reason}"))
          Status(error.status)(Json.toJson(multiError))
      }
    }
  }

}
