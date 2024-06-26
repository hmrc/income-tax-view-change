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

import play.api.libs.json.JsValue
import helpers.WiremockHelper

object DesCreateBusinessDetailsStub {

  val url: String => String = mtdbsaRef => s"""/income-tax/income-sources/mtdbsa/$mtdbsaRef/ITSA/business"""

  def stubPostDesBusinessDetails(mtdbsaRef: String, status: Int, request: JsValue, response: JsValue): Unit = {
    WiremockHelper.stubPost(url(mtdbsaRef), status, request.toString, response.toString)
  }

  def verifyCreateDesBusinessDetails(mtdbsaRef: String, requestBody: JsValue): Unit = {
    WiremockHelper.verifyPost(url(mtdbsaRef), requestBody.toString)
  }
}
