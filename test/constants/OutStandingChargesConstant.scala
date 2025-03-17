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

package constants

import models.outStandingCharges.{OutStandingCharge, OutstandingChargesSuccessResponse}
import play.api.libs.json.{JsValue, Json}

object OutStandingChargesConstant {
  val validOutStandingChargeJson: JsValue = Json.parse(
    """
      |  {
      |    "chargeName": "LATE",
      |    "relevantDueDate": "2021-01-31",
      |    "chargeAmount": 123456789012345.67,
      |    "tieBreaker": 1234
      |  }
      |""".stripMargin)

  val outStandingChargeModelOne = OutStandingCharge("LATE", Some("2021-01-31"), 123456789012345.67, 1234)
  val outStandingChargeModelTwo = OutStandingCharge("ACI", None, 12.67, 1234)


  val validSingleOutStandingChargeResponseJson: JsValue = Json.parse(
    """
      |  {
      |  "outstandingCharges": [{
      |       "chargeName": "LATE",
      |       "relevantDueDate": "2021-01-31",
      |       "chargeAmount": 123456789012345.67,
      |       "tieBreaker": 1234
      |       }
      |   ]
      |  }
      |""".stripMargin)


  val validMultipleOutStandingChargeResponseJson: JsValue = Json.parse(
    """
      |{
      |  "outstandingCharges": [{
      |         "chargeName": "LATE",
      |         "relevantDueDate": "2021-01-31",
      |         "chargeAmount": 123456789012345.67,
      |         "tieBreaker": 1234
      |       },
      |       {
      |         "chargeName": "ACI",
      |         "chargeAmount": 12.67,
      |         "tieBreaker": 1234
      |       }
      |  ]
      |}
      |""".stripMargin)

  val SingleOutStandingChargeResponseModel = OutstandingChargesSuccessResponse(List(outStandingChargeModelOne))
  val MultipleOutStandingChargeResponseModel = OutstandingChargesSuccessResponse(List(outStandingChargeModelOne, outStandingChargeModelTwo))

  val validMultipleOutStandingChargeDesResponseJson: JsValue = Json.parse(
    """
      |[{
      |     "chargeName": "LATE",
      |     "relevantDueDate": "2021-01-31",
      |     "chargeAmount": 123456789012345.67,
      |     "tieBreaker": 1234
      |   },
      |   {
      |     "chargeName": "ACI",
      |     "chargeAmount": 12.67,
      |     "tieBreaker": 1234
      |}]
      |""".stripMargin)
}
