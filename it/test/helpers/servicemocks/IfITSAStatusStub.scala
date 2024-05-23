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

import assets.ITSAStatusIntegrationTestConstants.{errorITSAStatusError, failedFutureITSAStatusError, taxYear, taxableEntityId}
import play.mvc.Http.Status
import helpers.WiremockHelper

object IfITSAStatusStub {

  def getITSAStatusUrl(taxableEntityId: String, taxYear: String, futureYears: Boolean = true, history: Boolean = true) = s"/income-tax/$taxableEntityId/person-itd/itsa-status/$taxYear?futureYears=$futureYears&history=$history"

  val url: String = getITSAStatusUrl(taxableEntityId, taxYear)

  def stubGetIfITSAStatusDetails(response: String): Unit = {
    WiremockHelper.stubGet(url, Status.OK, response)
  }

  def stubGetIfITSAStatusDetailsError(): Unit = {
    WiremockHelper.stubGet(url, Status.INTERNAL_SERVER_ERROR, failedFutureITSAStatusError.reason)
  }

  def stubGetIfITSAStatusDetailsBadRequest(): Unit = {
    WiremockHelper.stubGet(url, Status.BAD_REQUEST, errorITSAStatusError.reason)
  }

  def verifyGetIfITSAStatusDetails(): Unit = WiremockHelper.verifyGet(url)

}
