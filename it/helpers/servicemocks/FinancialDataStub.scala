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

import helpers.{IntegrationTestConstants, WiremockHelper}
import models.LastTaxCalculation
import play.api.http.Status

object FinancialDataStub {

  val url: (String, String, String) => String = (nino, year, calcType) =>
    s"""/income-tax-view-change-dynamic-stub/calculationstore/lastcalculation/$nino?year=$year&type=$calcType"""

  def stubGetFinancialData(nino: String, year: String, calcType: String, response: LastTaxCalculation): Unit = {
    val lastTaxCalculationResponse =
      IntegrationTestConstants.GetFinancialData
      .successResponse(response.calcID, response.calcTimestamp, response.calcAmount).toString

    WiremockHelper.stubGet(url(nino, year, calcType), Status.OK, lastTaxCalculationResponse)
  }

  def verifyGetFinancialData(nino: String, year: String, calcType: String): Unit =
    WiremockHelper.verifyGet(url(nino, year, calcType))

}
