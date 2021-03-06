/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.predicates

import config.MicroserviceAuthConnector
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.mvc._
import uk.gov.hmrc.auth.core.{AuthorisationException, AuthorisedFunctions}
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthenticationPredicate @Inject()(val authConnector: MicroserviceAuthConnector, cc: ControllerComponents
                                       ) extends BackendController(cc) with AuthorisedFunctions {

  def async(action: Request[AnyContent] => Future[Result]): Action[AnyContent] =
    Action.async { implicit request =>
      authorised() {
        action(request)
      } recover {
        case ex: AuthorisationException =>
          Logger.error(s"[AuthenticationPredicate][authenticated] Unauthorised Request to Backend. Propagating Unauthorised Response, ${ex.getMessage}")
          Unauthorized
      }
    }
}
