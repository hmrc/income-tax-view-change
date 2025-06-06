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
import play.api.Logging
import play.api.mvc._
import services.IncomeSourceDetailsService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}

@Singleton
class IncomeSourceDetailsController @Inject()(val authentication: AuthenticationPredicate,
                                              val incomeSourceDetailsService: IncomeSourceDetailsService,
                                              cc: ControllerComponents
                                             ) extends BackendController(cc) with Logging {

  def getNino(mtdRef: String): Action[AnyContent] = authentication.async { implicit request =>
    incomeSourceDetailsService.getNino(mtdRef)
  }

  def getIncomeSourceDetails(mtdRef: String): Action[AnyContent] = authentication.async { implicit request =>
    incomeSourceDetailsService.getIncomeSourceDetails(mtdRef)
  }
}
