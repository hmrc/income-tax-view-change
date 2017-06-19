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

package assets

import models.{LastTaxCalculation, LastTaxCalculationError}
import play.mvc.Http.Status

object TestConstants {
  object FinancialData {
    val testNino = "BB123456A"
    val testYear = "2018"
    val testCalcType = "it"

    val expectedLastTaxCalcResponse = LastTaxCalculation("testCalcId", "testTimestamp", 2345.67)

    val lastTaxCalculationError = LastTaxCalculationError(Status.INTERNAL_SERVER_ERROR, "Error Message")

  }
}
