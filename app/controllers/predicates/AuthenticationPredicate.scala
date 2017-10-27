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

package controllers.predicates

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.mvc.{Action, AnyContent, Request, Result}
import uk.gov.hmrc.auth.core.AuthorisedFunctions
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthenticationPredicate @Inject()(val authorisedFunctions: AuthorisedFunctions) extends BaseController {

  def async(action: Request[AnyContent] => Future[Result]): Action[AnyContent] =
    Action.async { implicit request =>
      authorisedFunctions.authorised() {
        action(request)
      } recoverWith {
        case _ =>
          Logger.debug("[AuthenticationPredicate][authenticated] Unauthorised Request to Backend. Propagating Unauthorised Response")
          Future.successful(Unauthorized)
      }
    }
}
