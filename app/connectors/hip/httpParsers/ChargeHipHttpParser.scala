/*
 * Copyright 2025 HM Revenue & Customs
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

package connectors.hip.httpParsers

import connectors.hip.httpParsers.errorResponses.ErrorResponseHttpParsers
import connectors.httpParsers.ChargeHttpParser.{ChargeResponseError, UnexpectedChargeErrorResponse, UnexpectedChargeResponse}
import models.financialDetails.hip.model.ChargesHipResponse
import models.hip.HipResponseErrorsObject
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, UNPROCESSABLE_ENTITY, NOT_FOUND}

object ChargeHipHttpParser extends ErrorResponseHttpParsers {

  import play.api.http.Status.OK
  import play.api.libs.json.{JsError, JsSuccess}
  import uk.gov.hmrc.http.{HttpReads, HttpResponse}

  type ChargeHipResponse = Either[ChargeResponseError, ChargesHipResponse]




  implicit object ChargeHipReads extends HttpReads[ChargeHipResponse] {
    override def read(method: String, url: String, response: HttpResponse): ChargeHipResponse = {
      response.status match {
        case OK =>
          logger.info(s"HipChargesResponse:::actual response ${response.body}")
          response.json.validate[ChargesHipResponse] match {
            case JsError(errors) =>
              logger.error("Unable to parse response into HipChargesResponse - " + errors)
              Left(UnexpectedChargeErrorResponse)

            case JsSuccess(value, _) =>
              Right(value)
          }
        case status if status == UNPROCESSABLE_ENTITY =>
          logger.info(s"$status returned from HiP with body: ${response.body}, checking for data not found scenario")
          handleUnprocessableStatusResponse(response)
        case status if status >= BAD_REQUEST && status < INTERNAL_SERVER_ERROR =>
          logger.error(s"$status returned from HiP with body: ${response.body}")
          Left(UnexpectedChargeResponse(status, response.body))
        case status =>
          logger.info(s"Unexpected Response from Hip with status: $status")
          Left(UnexpectedChargeErrorResponse)
      }
    }
  }

  private def handleUnprocessableStatusResponse(unprocessableResponse: HttpResponse): ChargeHipResponse = {
    unprocessableResponse.json.validate[HipResponseErrorsObject] match {
      case JsError(errors) =>
        logger.error("Unable to parse response as Business Validation Error - " + errors)
        logger.error(s"$unprocessableResponse.status returned from HiP with body: ${unprocessableResponse.body}")
        Left(UnexpectedChargeResponse(unprocessableResponse.status, unprocessableResponse.body))
      case JsSuccess(success, _) =>
        success match {
          case error: HipResponseErrorsObject if error.errors.code == "005" =>
            logger.info(s"Resource not found code identified, converting to 404 response")
            Left(UnexpectedChargeResponse(NOT_FOUND, unprocessableResponse.body))
          case _ =>
            logger.error(s"$unprocessableResponse.status returned from HiP with body: ${unprocessableResponse.body}")
            Left(UnexpectedChargeResponse(unprocessableResponse.status, unprocessableResponse.body))
        }
    }
  }
}
