/*
 * Copyright 2025 HM Revenue & Customs
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

package connectors

import java.time.LocalDate
import java.time.format.DateTimeFormatter

trait HipConnectors {

  // Headers constants
  val xMessageTypeFor1553 = "ETMPGetFinancialDetails"
  val xOriginatingSystem: String = "MDTP"
  val xTransmittingSystem : String = "HIP"

  // We should not get current date/time in our BE, but consume it from upstream
  def getMessageCreated: String = {
    val format = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ssZ")
    format.format(LocalDate.now)
  }

  // Query string param constants for API number: #5277
  val calculateAccruedInterest: String = "true"
  val customerPaymentInformation: String = "true"
  val removePaymentonAccount: String = "false"
  val idType: String = "NINO"
  val regimeType: String = "ITSA"

  val includeLocks: String = "true"
  val includeStatistical: String = "false"
}
