/*
 * Copyright 2026 HM Revenue & Customs
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

import connectors.hip.httpParsers.CalculationListLegacyHttpParser.handleErrorResponse
import connectors.hip.httpParsers.errorResponses.ErrorResponseHttpParsers
import connectors.httpParsers.RepaymentHistoryHttpParser.logger
import models.hip.HipResponseErrorsObject
import models.hip.repayments.SuccessfulRepaymentResponse
import play.api.http.Status.*
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
object RepaymentsHistoryDetailsHttpParser extends ErrorResponseHttpParsers{

  given RepaymentsHistoryDetailsReads: HttpReads[HttpGetResult[SuccessfulRepaymentResponse]] with {

    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[SuccessfulRepaymentResponse] =
      response.status match {
        case OK =>
          logger.info("successfulely parsed response to List[RepaymentHistory]")
          Right(response.json.as[SuccessfulRepaymentResponse])
        case status if status == UNPROCESSABLE_ENTITY =>
          logger.info(s"$status returned from HiP with body: ${response.body}, checking for data not found scenario")
          handleUnprocessableStatusResponse(response)
        case status =>
          logger.error(s"Call to RepaymentsHistory failed with status: $status and response body: ${response.body}")
          handleErrorResponse(response)
      }
  }

  private def handleUnprocessableStatusResponse(unprocessableResponse: HttpResponse): SuccessfulRepaymentResponse = {
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
