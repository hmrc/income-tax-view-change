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

package helpers.servicemocks

import helpers.WiremockHelper
import play.mvc.Http.Status

object HipUpdateCustomerFactStub {

  val url: String = "/etmp/RESTAdapter/customer/facts"

  private def requestBody(mtdId: String): String =
    s"""
       |{
       |  "idType": "MTDBSA",
       |  "idValue": "$mtdId",
       |  "regimeType": "ITSA",
       |  "factId": "ZORIGIN",
       |  "value": "C"
       |}
       |""".stripMargin

  def stubPutUpdateCustomerFactSuccess(mtdId: String): Unit =
    WiremockHelper.stubPut(
      url,
      Status.OK,
      requestBody(mtdId),
      """{"processingDateTime":"2022-01-31T09:26:17Z"}"""
    )

  def stubPutUpdateCustomerFactUnprocessable(mtdId: String): Unit =
    WiremockHelper.stubPut(
      url,
      Status.UNPROCESSABLE_ENTITY,
      requestBody(mtdId),
      """{"errors":{"processingDate":"2001-12-17T09:30:47Z","code":"001","text":"Regime missing or invalid"}}"""
    )

  def verifyPutUpdateCustomerFact(mtdId: String): Unit =
    WiremockHelper.verifyPut(url, requestBody(mtdId))
}
