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

import play.api.libs.json.{JsValue, Json}
import helpers.WiremockHelper

object DesOutStandingChargesStub {
  private def url(idType: String, idNumber: String, taxYearEndDate: String): String = {
    s"/income-tax/charges/outstanding/$idType/$idNumber/$taxYearEndDate"
  }

  def stubGetOutStandingChargeDetails(idType: String, idNumber: String, taxYearEndDate: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    WiremockHelper.stubGet(url(idType, idNumber, taxYearEndDate), status, response.toString)
  }

}
