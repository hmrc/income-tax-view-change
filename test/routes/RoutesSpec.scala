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

package routes

import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class RoutesSpec extends UnitSpec with WithFakeApplication {

  val contextRoute: String = "/income-tax-view-change"

  // Estimated Tax Liability routes
  "The URL for the EstimatedTaxLiabilityController.getEstimateTaxLiability action" should {
    s"be equal to $contextRoute/estimated-tax-liability" in {
      controllers.routes.EstimatedTaxLiabilityController.getEstimatedTaxLiability().url shouldBe s"$contextRoute/estimated-tax-liability"
    }
  }
}