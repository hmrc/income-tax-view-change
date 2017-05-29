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

package controllers

import helpers.ComponentSpecBase
import helpers.servicemocks.{AuthStub, FinancialDataStub}
import play.api.http.Status
import helpers.IntegrationTestConstants._
import models.EstimatedTaxLiability

class EstimatedTaxLiabilityControllerSpec extends ComponentSpecBase {
  "subscribe" should {
    "call the subscription service successfully when auth succeeds" in {
      AuthStub.stubAuthorised()

      FinancialDataStub.stubGetFinancialData(testMtditid, 1000.00, 300.00, 500.00)

      val res = IncomeTaxViewChange.getEstimatedTaxLiability(testMtditid)

      FinancialDataStub.verifyGetFinancialData(testMtditid)

      res.status shouldBe Status.OK
      res.json.as[EstimatedTaxLiability] shouldBe EstimatedTaxLiability(
        total = 1800,
        incomeTax = 1000,
        nic2 = 300,
        nic4 = 500
      )
    }
    "fail when get authority fails" in {
      AuthStub.stubUnatuhorised()

      val res = IncomeTaxViewChange.getEstimatedTaxLiability(testMtditid)

      res.status shouldBe Status.UNAUTHORIZED
      res.body shouldBe empty
    }
  }
}
