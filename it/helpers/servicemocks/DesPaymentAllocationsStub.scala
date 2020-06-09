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

object DesPaymentAllocationsStub {

  private def url(nino: String, paymentLot: String, paymentLotItem: String): String = {
    s"/cross-regime/payment-allocation/NINO/$nino/ITSA?paymentLot=$paymentLot&paymentLotItem=$paymentLotItem"
  }

  def stubGetPaymentAllocations(nino: String, paymentLot: String, paymentLotItem: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    WiremockHelper.stubGet(url(nino, paymentLot, paymentLotItem), status, response.toString)
  }

  def verifyGetPaymentAllocations(nino: String, paymentLot: String, paymentLotItem: String): Unit = {
    WiremockHelper.verifyGet(url(nino, paymentLot, paymentLotItem))
  }

}
