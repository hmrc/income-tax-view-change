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

import java.time.LocalDateTime

trait HipConnectors {

  // Headers constants
  val xMessageTypeFor5277 = "ETMPGetFinancialDetails" // <= HiP version for 1553
  val xOriginatingSystem: String = "MDTP"
  val xTransmittingSystem : String = "HIP"

  // Current date/time should be consumed from upstream
  def getMessageCreated: String = LocalDateTime.now.toString

  // Query string param constants for API number: #5277/1553
  val calculateAccruedInterest: String = "true"
  val customerPaymentInformation: String = "true"
  val removePaymentonAccount: String = "false"
  val idType: String = "NINO"
  val regimeType: String = "ITSA"

  val includeLocks: String = "true"
  val includeStatistical: String = "false"
}
