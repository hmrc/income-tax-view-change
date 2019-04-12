/*
 * Copyright 2019 HM Revenue & Customs
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

package models.PreviousCalculation

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class EoyEstimate(incomeTaxNicAmount: BigDecimal)

case class CalcResult(incomeTaxNicYtd: BigDecimal,
                      eoyEstimate: Option[EoyEstimate] = None,
                      nationalRegime: Option[String] = None,
                      totalTaxableIncome: Option[BigDecimal] = None,
                      annualAllowances: Option[AnnualAllowancesModel] = None,
                      incomeTax: Option[IncomeTaxModel] = None,
                      taxableIncome: Option[TaxableIncomeModel] = None,
                      nic: Option[NicModel] = None
                     )

case class PreviousCalculationModel(calcOutput: CalcOutput)

case class AnnualAllowancesModel(personalAllowance: Option[BigDecimal] = None)

object AnnualAllowancesModel {
  implicit val formats: OFormat[AnnualAllowancesModel] = Json.format[AnnualAllowancesModel]
}

case class TaxableIncomeModel(totalIncomeAllowancesUsed: Option[BigDecimal],
                              incomeReceived: Option[IncomeReceivedModel])

object TaxableIncomeModel {
  implicit val formats: OFormat[TaxableIncomeModel] = Json.format[TaxableIncomeModel]
}

case class IncomeTaxModel(totalAllowancesAndReliefs: Option[BigDecimal],
                          payAndPensionsProfit: Option[PayPensionsProfitModel],
                          dividends: Option[DividendsModel],
                          savingsAndGains: Option[SavingsAndGainsModel],
                          giftAid: Option[GiftAidModel])

object IncomeTaxModel {
  implicit val reads: Reads[IncomeTaxModel] = (
    (__ \ "totalAllowancesAndReliefs").readNullable[BigDecimal] and
      (__ \ "payPensionsProfit").readNullable[PayPensionsProfitModel] and
      (__ \ "dividends").readNullable[DividendsModel] and
      (__ \ "savingsAndGains").readNullable[SavingsAndGainsModel] and
      (__ \ "giftAid").readNullable[GiftAidModel]
    ) (IncomeTaxModel.apply _)

  implicit val writes: Writes[IncomeTaxModel] = (
    (__ \ "totalAllowancesAndReliefs").writeNullable[BigDecimal] and
      (__ \ "payPensionsProfit").writeNullable[PayPensionsProfitModel] and
      (__ \ "dividends").writeNullable[DividendsModel] and
      (__ \ "savingsAndGains").writeNullable[SavingsAndGainsModel] and
      (__ \ "giftAid").writeNullable[GiftAidModel]
    ) (unlift(IncomeTaxModel.unapply))
}

case class IncomeReceivedModel(selfEmploymentIncome: Option[BigDecimal],
                               ukPropertyIncome: Option[BigDecimal],
                               bbsiIncome: Option[BigDecimal],
                               ukDividendIncome: Option[BigDecimal]
                              )

object IncomeReceivedModel {
  implicit val formats: OFormat[IncomeReceivedModel] = Json.format[IncomeReceivedModel]
}

case class SavingsAndGainsModel(totalAmount: BigDecimal,
                                taxableIncome: BigDecimal,
                                band: Seq[BandModel])

object SavingsAndGainsModel {
  implicit val formats: OFormat[SavingsAndGainsModel] = Json.format[SavingsAndGainsModel]
}

case class DividendsModel(totalAmount: BigDecimal,
                          taxableIncome: BigDecimal,
                          band: Seq[BandModel])

object DividendsModel {
  implicit val formats: OFormat[DividendsModel] = Json.format[DividendsModel]
}

case class PayPensionsProfitModel(totalAmount: BigDecimal,
                          taxableIncome: BigDecimal,
                          band: Seq[BandModel])

object PayPensionsProfitModel {
  implicit val formats: OFormat[PayPensionsProfitModel] = Json.format[PayPensionsProfitModel]
}

case class GiftAidModel (paymentsMade: BigDecimal,
                         rate: BigDecimal,
                         taxableAmount: BigDecimal)

object GiftAidModel {
  implicit val formats: OFormat[GiftAidModel] = Json.format[GiftAidModel]
}

case class BandModel(income: BigDecimal,
                     rate: BigDecimal,
                     taxAmount: BigDecimal,
                     name: String = "")

object BandModel {
  implicit val formats: OFormat[BandModel] = Json.format[BandModel]
}

case class CalcOutput(calcID: String,
                      calcAmount: Option[BigDecimal] = None,
                      calcTimestamp: Option[String] = None,
                      crystallised: Option[Boolean] = None,
                      calcResult: Option[CalcResult] = None)

case class NicModel(class2: Option[BigDecimal],
                    class4: Option[BigDecimal])

object NicModel {
  implicit val reads: Reads[NicModel] = (
    (__ \ "class2" \ "amount").readNullable[BigDecimal] and
      (__ \ "class4" \ "totalAmount").readNullable[BigDecimal]
    ) (NicModel.apply _)
  implicit val writes: Writes[NicModel] = (
    (__ \ "class2" \ "amount").writeNullable[BigDecimal] and
      (__ \ "class4" \ "totalAmount").writeNullable[BigDecimal]
    ) (unlift(NicModel.unapply))
}

object EoyEstimate {
  implicit val format: OFormat[EoyEstimate] = Json.format[EoyEstimate]
}

object CalcResult {
  implicit val format: OFormat[CalcResult] = Json.format[CalcResult]
}

object PreviousCalculationModel {
  implicit val format: OFormat[PreviousCalculationModel] = Json.format[PreviousCalculationModel]
}

object CalcOutput {
  implicit val format: OFormat[CalcOutput] = Json.format[CalcOutput]
}
