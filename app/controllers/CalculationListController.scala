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
import models.errors.{Error, InvalidNino, InvalidTaxYear, MultiError}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import services.CalculationListService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.urlUtils.{isInvalidNino, isInvalidTaxYearEnd, isInvalidTaxYearRange}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationListController @Inject()(val authentication: AuthenticationPredicate,
                                          val calculationListService: CalculationListService,
                                          cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) with Logging {
  // 1404
  def getCalculationList(nino: String, taxYearEnd: String): Action[AnyContent] = authentication.async {
    implicit request =>
      if (isInvalidNino(nino)) {
        logger.error(s"[CalculationListController][getCalculationList] Invalid Nino '$nino' received in request.")
        Future.successful(BadRequest(Json.toJson[Error](InvalidNino)))
      } else if (isInvalidTaxYearEnd(taxYearEnd)) {
        logger.error(s"[CalculationListController][getCalculationList] Invalid tax year '$taxYearEnd' received in request.")
        Future.successful(BadRequest(Json.toJson[Error](InvalidTaxYear)))
      } else {
        getCalculationList(nino, taxYearEnd)
      }
  }

  // 1896
  def getCalculationList2324(nino: String, taxYearRange: String): Action[AnyContent] = authentication.async {
    implicit request =>
      if (isInvalidNino(nino)) {
        logger.error(s"[CalculationListController][getCalculationList2324] Invalid Nino '$nino' received in request.")
        Future.successful(BadRequest(Json.toJson[Error](InvalidNino)))
      } else if (isInvalidTaxYearRange(taxYearRange)) {
        logger.error(s"[CalculationListController][getCalculationList2324] Invalid Tax Year '$taxYearRange' received in request.")
        Future.successful(BadRequest(Json.toJson[Error](InvalidTaxYear)))
      } else {
        getCalculationList2324(nino, taxYearRange)
      }
  }

  private def getCalculationList(nino: String, taxYear: String)(implicit hc: HeaderCarrier): Future[Result] = {
    logger.debug(s"[CalculationListController][getCalculationList] Calling CalculationListService.getCalculationList")
    calculationListService.getCalculationList(nino, taxYear).map {
      case Right(calculationList) =>
        val calculation = calculationList.calculations.head
        Ok(Json.toJson(calculation))
      case Left(error) => error.error match {
        case singleError: Error =>
          logger.error(s"[CalculationListController][getCalculationList] returned a single error ${singleError.reason}")
          Status(error.status)(Json.toJson(singleError))
        case multiError: MultiError =>
          multiError.failures.foreach(singleError =>
            logger.error(s"[CalculationListController][getCalculationList] returned multiple errors ${singleError.reason}"))
          Status(error.status)(Json.toJson(multiError))
      }
    }
  }

  private def getCalculationList2324(nino: String, taxYear: String)(implicit hc: HeaderCarrier): Future[Result] = {
    logger.debug(s"[CalculationListController][getCalculationList] Calling CalculationListService.getCalculationList")
    calculationListService.getCalculationList2324(nino, taxYear).map {
      case Right(calculationList) =>
        val calculation = calculationList.calculations.head
        Ok(Json.toJson(calculation))
      case Left(error) => error.error match {
        case singleError: Error =>
          logger.error(s"[CalculationListController][getCalculationList] returned a single error ${singleError.reason}")
          Status(error.status)(Json.toJson(singleError))
        case multiError: MultiError =>
          multiError.failures.foreach(singleError =>
            logger.error(s"[CalculationListController][getCalculationList] returned multiple errors ${singleError.reason}"))
          Status(error.status)(Json.toJson(multiError))
      }
    }
  }

}
