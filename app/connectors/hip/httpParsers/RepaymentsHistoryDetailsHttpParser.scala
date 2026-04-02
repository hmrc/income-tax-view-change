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
import models.hip.{ErrorResponse, HipRepaymentResponseError, HipRepaymentResponseErrorsObject}
import models.hip.repayments.SuccessfulRepaymentResponse
import play.api.http.Status.*
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import play.api.libs.json.{JsError, JsSuccess, Json}

object RepaymentsHistoryDetailsHttpParser extends ErrorResponseHttpParsers {

  given RepaymentsHistoryDetailsReads: HttpReads[HttpGetResult[SuccessfulRepaymentResponse]] with {

    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[SuccessfulRepaymentResponse] = {

      response.status match {

        case OK =>
          logger.info("Successfully parsed response to List[RepaymentHistory]")
          Right(response.json.as[SuccessfulRepaymentResponse])

        case UNPROCESSABLE_ENTITY =>
          handleUnprocessableEntity(response)

        case status =>
          logger.error(s"Call to RepaymentsHistory failed with status: $status, body: ${response.body}")
          handleErrorResponse(response)
      }
    }

    private def handleUnprocessableEntity(response: HttpResponse): HttpGetResult[SuccessfulRepaymentResponse] = {

      response.json.validate[HipRepaymentResponseErrorsObject] match {

        case JsError(errors) =>
          logger.error("[RepaymentsHistoryDetailsHttpParser] - Unable to process 422 error from HIP Repayment History: " + errors)
          handleErrorResponse(response)

        case JsSuccess(errorObj, _) =>
          if (hasError001(errorObj.etmp_transaction_header)) {
            logger.info(s"[RepaymentsHistoryDetailsHttpParser] HIP Repayment History API could not find this customer, converting to 404 response")
            Left(ErrorResponse.GenericError(NOT_FOUND, Json.toJson("")))
          } else {
            logger.error(s"[RepaymentsHistoryDetailsHttpParser] HIP Repayment History API provided 422 response that wasn't a No data found.  Instead got: ${errorObj.etmp_transaction_header}")
            handleErrorResponse(response)
          }
      }
    }

    private def hasError001(hipError: HipRepaymentResponseError): Boolean = {
      hipError.status == "NOT_OK" &&
        hipError.returnParameters.exists { params =>
          params.exists(p => p.paramName == "ERRORCODE" && p.paramValue == "001")
        }
    }
  }
}