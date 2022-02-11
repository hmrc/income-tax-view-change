/*
 * Copyright 2022 HM Revenue & Customs
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

package routes

import utils.TestSupport

class RoutesSpec extends TestSupport {

  val contextRoute: String = "/income-tax-view-change"

  val testNino = "BB123456A"
  val testYear = "2018"
  val testCalcType = "it"
  val testIncomeSourceID = "XAIS123456"

  // Open Report Deadlines routes
  "The URL for the ReportDeadlinesController.getOpenObligations action" should {
    s"be equal to $contextRoute/$testNino/$testIncomeSourceID/report-deadlines" in {
      controllers.routes.ReportDeadlinesController.getOpenObligations(testNino).url shouldBe
        s"$contextRoute/$testNino/report-deadlines"
    }
  }

  // Fulfilled Report Deadlines routes
  "The URL for the ReportDeadlinesController.getFulfilledObligations action" should {
    s"be equal to $contextRoute/$testNino/$testIncomeSourceID/report-deadlines" in {
      controllers.routes.ReportDeadlinesController.getFulfilledObligations(testNino).url shouldBe
        s"$contextRoute/$testNino/fulfilled-report-deadlines"
    }
  }

  // Previous Calculation routes
  "The URL for the PreviousCalculationController.getPreviousCalculation action" should {
    s"be equal to $contextRoute/previous-tax-calculation/$testNino/$testYear" in {
      controllers.routes.PreviousCalculationController.getPreviousCalculation(testNino, testYear).url shouldBe
        s"$contextRoute/previous-tax-calculation/$testNino/$testYear"
    }
  }
}
