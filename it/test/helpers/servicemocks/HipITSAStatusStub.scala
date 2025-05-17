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

import constants.ITSAStatusIntegrationTestConstants.{errorITSAStatusError, failedFutureITSAStatusError, taxYear, taxableEntityId}
import helpers.WiremockHelper
import play.mvc.Http.Status

object HipITSAStatusStub {

  def getITSAStatusUrl(taxableEntityId: String, taxYear: String): String =
    s"/itsd/person-itd/itsa-status/$taxableEntityId?taxYear=$taxYear&futureYears=true&history=true"

  def updateItsStatusUrl(taxableEntityId: String) = s"/itsd/itsa-status/update/$taxableEntityId"

  val url: String = getITSAStatusUrl(taxableEntityId, taxYear)
  val updateUrl: String = updateItsStatusUrl(taxableEntityId)

  def stubGetHipITSAStatusDetails(response: String): Unit = {
    WiremockHelper.stubGet(url, Status.OK, response)
  }

  def stubPutHipITSAStatusUpdate(statusInt: Int, response: String, headers: Map[String, String] = Map.empty): Unit = {
    WiremockHelper.stubPutWithHeaders(updateUrl, statusInt, response, headers)
  }

  def stubGetHipITSAStatusDetailsError(): Unit = {
    WiremockHelper.stubGet(url, Status.INTERNAL_SERVER_ERROR, failedFutureITSAStatusError.reason)
  }

  def stubGetHipITSAStatusDetailsBadRequest(): Unit = {
    WiremockHelper.stubGet(url, Status.BAD_REQUEST, errorITSAStatusError.reason)
  }

  def verifyGetHipITSAStatusDetails(): Unit = WiremockHelper.verifyGet(url)

}
