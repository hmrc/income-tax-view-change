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

import connectors.httpParsers.ChargeHttpParser.UnexpectedChargeResponse
import controllers.predicates.AuthenticationPredicate
import play.api.Logging
import play.api.mvc._
import services.FinancialDetailService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class FinancialDetailCreditsController @Inject()(authentication: AuthenticationPredicate,
                                                 cc: ControllerComponents,
                                                 financialDetailChargesService : FinancialDetailService)
                                                (implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def getCredits(nino: String, fromDate: String, toDate: String): Action[AnyContent] = {
    authentication.async { implicit request =>
      financialDetailChargesService.getCredits(
        nino,
        fromDate,
        toDate
      ) map {
        case Right(creditsAsJson) =>
          logger.debug("Successful Response: " + creditsAsJson)
          Ok(creditsAsJson)
        case Left(error: UnexpectedChargeResponse) if error.code == NOT_FOUND =>
          logger.info("404: " + error)
          Status(error.code)(error.response)
        case Left(error: UnexpectedChargeResponse) if error.code >= BAD_REQUEST && error.code < INTERNAL_SERVER_ERROR =>
          logger.error("error: " + error)
          Status(error.code)(error.response)
        case Left(otherError) =>
          logger.error("other error: " + otherError)
          InternalServerError("Failed to retrieve charge details")
      }
    }
  }
}
