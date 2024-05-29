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

import connectors.itsastatus.ITSAStatusConnector
import connectors.itsastatus.ITSAStatusConnector.CorrelationIdHeader
import connectors.itsastatus.OptOutUpdateRequestModel._
import controllers.predicates.AuthenticationPredicate
import models.itsaStatus.{ITSAStatusResponseError, ITSAStatusResponseNotFound}
import org.apache.pekko.util.ByteString
import play.api.Logging
import play.api.http.HttpEntity
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, ResponseHeader, Result}
import play.mvc.Http
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ITSAStatusController @Inject()(authentication: AuthenticationPredicate,
                                     cc: ControllerComponents,
                                     connector: ITSAStatusConnector)
                                    (implicit ec: ExecutionContext) extends BackendController(cc) with Logging {

  def getITSAStatus(taxableEntityId: String, taxYear: String, futureYears: Boolean,
                    history: Boolean): Action[AnyContent] = authentication.async { implicit request =>
    connector.getITSAStatus(
      taxableEntityId = taxableEntityId,
      taxYear = taxYear,
      futureYears = futureYears, history = history).map {
      case Left(error: ITSAStatusResponseNotFound) =>
        logger.warn(s"ITSA Status not found: $error")
        Status(error.status)(Json.toJson(error))
      case Left(error: ITSAStatusResponseError) =>
        logger.error(s"Error Response: $error")
        Status(error.status)(Json.toJson(error))
      case Left(error) =>
        logger.error(s"Error fetching ITSA Status: $error")
        InternalServerError("[ITSAStatusController][getITSAStatus]")
      case Right(result) =>
        logger.debug(s"Successful Response: $result")
        Ok(Json.toJson(result))
    }
  }

  def updateItsaStatus(taxableEntityId: String): Action[AnyContent] = authentication.async { implicit request =>

    def toResult(fResponse: Future[OptOutUpdateResponse]): Future[Result] = {
      fResponse.map {

        case success: OptOutUpdateResponseSuccess => new Result(ResponseHeader(Http.Status.NO_CONTENT,
          Map(CorrelationIdHeader -> success.correlationId)), HttpEntity.NoEntity, None)

        case fail: OptOutUpdateResponseFailure => new Result(ResponseHeader(fail.statusCode,
          Map(CorrelationIdHeader -> fail.correlationId)),
          HttpEntity.Strict(ByteString(Json.toJson(fail).toString()), None))

      }
    }

    val connectorResponse = for {
      json <- request.body.asJson
      optOutUpdateRequest <- json.validate[OptOutUpdateRequest].asOpt
    } yield {
      connector.requestOptOutForTaxYear(taxableEntityId, optOutUpdateRequest)
    }

    connectorResponse.map(toResult).getOrElse(toResult(Future.successful(OptOutUpdateResponseFailure.defaultFailure())))

  }

}
