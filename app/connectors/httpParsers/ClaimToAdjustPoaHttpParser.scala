/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors.httpParsers

import models.claimToAdjustPoa.ClaimToAdjustPoaApiResponse._
import models.claimToAdjustPoa.ClaimToAdjustPoaResponse.{ClaimToAdjustPoaResponse, ErrorResponse}
import play.api.Logger
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR}
import play.api.libs.json.JsSuccess
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object ClaimToAdjustPoaHttpParser {

    implicit object ClaimToAdjustPoaResponseReads extends HttpReads[ClaimToAdjustPoaResponse] {

        override def read(method: String, url: String, response: HttpResponse): ClaimToAdjustPoaResponse = {
                (response.status match {
            case CREATED =>
                response.json.validate[ClaimToAdjustPoaApiSuccess] match {
                case JsSuccess(model, _) =>
                    ClaimToAdjustPoaResponse(CREATED, Right(model.successResponse))
                case _ => {
                    Logger("application").warn(s"[ClaimToAdjustPoaResponseReads] Invalid JSON in Claim To Adjust POA success response")
                    ClaimToAdjustPoaResponse(INTERNAL_SERVER_ERROR,
                        Left(ErrorResponse("Invalid JSON in success response")))
                }
            }
            case status =>
                response.json.validate[ClaimToAdjustPoaApiFailure] match {
                    case JsSuccess(model, _) =>
                        ClaimToAdjustPoaResponse(status = status, Left(ErrorResponse(model.toString)))
                    case _ =>
                        Logger("application").warn(s"[ClaimToAdjustPoaResponseReads] Invalid JSON in Claim To Adjust POA failure response")
                        ClaimToAdjustPoaResponse(INTERNAL_SERVER_ERROR,
                            Left(ErrorResponse("Invalid JSON in failure response")))
                }
          })
        }
    }
}
