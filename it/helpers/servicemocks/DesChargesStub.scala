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

package helpers.servicemocks

import helpers.WiremockHelper
import play.api.libs.json.{JsValue, Json}

object DesChargesStub {

  private def url(nino: String, from: String, to: String): String = {
    s"/enterprise/02.00.00/financial-data/NINO/$nino/ITSA?dateFrom=$from&dateTo=$to&onlyOpenItems=false&includeLocks=true&calculateAccruedInterest=true&removePOA=false&customerPaymentInformation=true&includeStatistical=false"
  }

  def stubGetChargeDetails(nino: String, from: String, to: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    WiremockHelper.stubGet(url(nino, from, to), status, response.toString)
  }
}
