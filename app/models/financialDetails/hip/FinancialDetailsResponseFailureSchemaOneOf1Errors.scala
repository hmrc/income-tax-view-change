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

package models.financialDetails.hip.model



case class FinancialDetailsResponseFailureSchemaOneOf1Errors(
  processingDate: String,
  code: ItsaFinancialDetailsQueryResponseFailureSchemaOneOf1ErrorsEnums.Code,
  text: String
)

object ItsaFinancialDetailsQueryResponseFailureSchemaOneOf1ErrorsEnums {

  type Code = Code.Value
  object Code extends Enumeration {
    val `002` = Value("002")
    val `003` = Value("003")
    val `005` = Value("005")
    val `015` = Value("015")
  }

}
