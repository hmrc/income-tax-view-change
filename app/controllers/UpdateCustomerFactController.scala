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

import connectors.hip.UpdateCustomerFactConnector
import controllers.predicates.AuthenticationPredicate
import models.hip.updateCustomerFact.{UpdateCustomerFactFailure, UpdateCustomerFactSuccess}
import play.api.Logging
import play.api.mvc.Results.Status
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class UpdateCustomerFactController @Inject()(authentication: AuthenticationPredicate,
                                             cc: ControllerComponents,
                                             connector: UpdateCustomerFactConnector)
                                            (implicit ec: ExecutionContext)
  extends BackendController(cc) with Logging {


  def updateKnownFacts(mtdId: String): Action[AnyContent] = authentication.async { implicit request =>
    connector.updateCustomerFactsToConfirmed(mtdId).map {
      case UpdateCustomerFactSuccess(json, _) => Ok(json)
      case UpdateCustomerFactFailure(status, body, _) => Status(status)(body)
    }
  }
}
