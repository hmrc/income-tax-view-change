/*
 * Copyright 2023 HM Revenue & Customs
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

package utils

object urlUtils {

  def isInvalidNino(nino: String): Boolean = {
    val desNinoRegex = "^((?!(BG|GB|KN|NK|NT|TN|ZZ)|(D|F|I|Q|U|V)[A-Z]|[A-Z](D|F|I|O|Q|U|V))[A-Z]{2})[0-9]{6}[A-D]?$"
    !nino.matches(desNinoRegex)
  }

  def isInvalidTaxYearEnd(taxYear: String): Boolean = {
    val desTaxYearPattern = "^(201[6-9]|20[2-9][0-9]|2[1-9][0-9]{2})$"
    !taxYear.matches(desTaxYearPattern)
  }

  def isInvalidTaxYearRange(taxYear: String): Boolean = {
    val desTaxYearPattern = """^\d{2}-(\d{2})$"""
    val isValidFormat = taxYear.matches(desTaxYearPattern)

    if (isValidFormat) {
      val taxYearFrom = taxYear.take(2).toInt
      val taxYearTo = taxYear.drop(3).toInt
      taxYearTo != taxYearFrom + 1
    } else {
      true
    }
  }

}
