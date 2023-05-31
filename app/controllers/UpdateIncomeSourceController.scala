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

import connectors.UpdateIncomeSourceConnector
import controllers.predicates.AuthenticationPredicate
import models.updateIncomeSource.request.{UpdateIncomeSourceRequestError, UpdateIncomeSourceRequestModel}
import models.updateIncomeSource.{UpdateIncomeSourceResponseError, UpdateIncomeSourceResponseModel}
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdateIncomeSourceController @Inject()(authentication: AuthenticationPredicate,
                                             cc: ControllerComponents,
                                             connector: UpdateIncomeSourceConnector)
                                            (implicit ec: ExecutionContext) extends BackendController(cc) with Logging {


  def updateCessationDate(): Action[AnyContent] =
    authentication.async {
      implicit request =>
        request.body.asJson.getOrElse(Json.obj()).validate[UpdateIncomeSourceRequestModel] fold(
          invalid => {
            logger.error(s"[UpdateIncomeSourceController][updateCessationDate] - Validation Errors: $invalid")
            UpdateIncomeSourceRequestError("Json validation error while parsing request")
          },
          valid => {
            logger.info(s"[UpdateIncomeSourceController][updateCessationDate] - successfully parsed response to UpdateIncomeSourceRequestModel")
            valid
          }
        ) match {
          case x: UpdateIncomeSourceRequestError =>
            logger.error(s"[UpdateIncomeSourceController][updateCessationDate] - Bad Request")
            Future(BadRequest(Json.toJson(x)))
          case x: UpdateIncomeSourceRequestModel => connector.updateIncomeSource(x).map {
            case error: UpdateIncomeSourceResponseError =>
              logger.error(s"[UpdateIncomeSourceController][updateCessationDate] - Error Response: $error")
              Status(error.status)(Json.toJson(error))
            case success: UpdateIncomeSourceResponseModel =>
              logger.debug(s"[UpdateIncomeSourceController][updateCessationDate] - Successful Response: $success")
              Ok(Json.toJson(success))
          }
        }

    }

}
