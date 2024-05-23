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

import assets.BusinessDetailsIntegrationTestConstants._
import models.incomeSourceDetails.IncomeSourceDetailsModel
import play.api.http.Status
import helpers.WiremockHelper

object BusinessDetailsStub {

  val ifurl: (String) => String = (mtdRef) => s"""/registration/business-details/mtdId/$mtdRef"""

  def stubGetIfBusinessDetails(mtdRef: String, response: IncomeSourceDetailsModel): Unit = {
    val ifBusinessDetailsResponse = successResponseIf(response.nino).toString
    WiremockHelper.stubGet(ifurl(mtdRef), Status.OK, ifBusinessDetailsResponse)
  }

  def stubGetBusinessDetailsError(mtdRef: String): Unit = {
    val errorResponse = failureResponse("500", "ISE")
    WiremockHelper.stubGet(ifurl(mtdRef), Status.INTERNAL_SERVER_ERROR, errorResponse.toString)
  }

  def verifyGetIfBusinessDetails(mtdRef: String): Unit =
    WiremockHelper.verifyGet(ifurl(mtdRef))

}
