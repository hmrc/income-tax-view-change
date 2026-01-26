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

package models.hip

sealed trait HipApi {
  val name: String
}

case object GetCalcListTYSHipApi extends HipApi {
  val name = "get-calc-list-TYS"
  def apply(): String = name
}

case object GetLegacyCalcListHipApi extends HipApi {
  val name = "get-legacy-calc-list"
  def apply(): String = name
}

case object GetBusinessDetailsHipApi extends HipApi {
  val name = "get-business-details"
  def apply(): String = name
}

case object ITSAStatusHipApi extends HipApi {
  val name = "get-itsa-status"
  def apply(): String = name
}

case object GetFinancialDetailsHipApi extends HipApi {
  val name = "get-financial-details"
  def apply(): String = name
}

case object CreateIncomeSourceHipApi extends HipApi {
  val name = "create-income-source"
  def apply(): String = name
}

case object GetChargeHistoryHipApi extends HipApi {
  val name = "get-charge-history"
  def apply(): String = name
}

case object UpdateCustomerFactHipApi extends HipApi {
  val name = "update-customer-fact"
  def apply(): String = name
}
