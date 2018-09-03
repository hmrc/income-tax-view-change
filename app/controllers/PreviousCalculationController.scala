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
import models.PreviousCalculation._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services.CalculationService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PreviousCalculationController @Inject()(val authentication: AuthenticationPredicate,
                                              val calculationService: CalculationService
                                             ) extends BaseController {

  def getPreviousCalculation(nino: String, year: String): Action[AnyContent] =
    authentication.async {
      implicit request =>
        if (isInvalidNino(nino)) {
          Logger.warn(s"[PreviousCalculationController][getPreviousCalculation] Invalid Nino '$nino' received in request.")
          Future.successful(BadRequest(Json.toJson(InvalidNino)))
        } else {
          getPreviousCalculation(nino, year)
        }
    }

  private def getPreviousCalculation(nino: String, year: String)(implicit hc: HeaderCarrier) = {
    Logger.debug(s"[PreviousCalculationController][getPreviousCalculation] Calling CalculationService.getPreviousCalculation")
    calculationService.getPreviousCalculation(nino, year).map {
      case _@Right(previousCalculation) => Ok(Json.toJson(previousCalculation))
      case _@Left(error) => error.error match {
        case singleError: Error => Status(error.status)(Json.toJson(singleError))
        case multiError: MultiError => Status(error.status)(Json.toJson(multiError))
      }
    }
  }

  private def isInvalidNino(nino: String): Boolean = {
    val desNinoRegex = "^((?!(BG|GB|KN|NK|NT|TN|ZZ)|(D|F|I|Q|U|V)[A-Z]|[A-Z](D|F|I|O|Q|U|V))[A-Z]{2})[0-9]{6}[A-D]?$"
    !nino.matches(desNinoRegex)
  }

}
