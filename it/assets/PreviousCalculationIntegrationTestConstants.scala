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

import models.PreviousCalculation._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.mvc.Http.Status

object PreviousCalculationIntegrationTestConstants {

  val previousCalculation: PreviousCalculationModel = PreviousCalculationModel(
    CalcOutput(calcID = "12345678", calcAmount = Some(22.56), calcTimestamp = Some("4498-07-06T21:42:24.294Z"), crystallised = Some(true),
      calcResult = Some(CalcResult(
        incomeTaxNicYtd = 500.68,
        eoyEstimate = Some(EoyEstimate(125.63)),
        nationalRegime = Some("UK"),
        totalTaxableIncome = Some(1000.25),
        annualAllowances = Some(AnnualAllowancesModel(Some(99999999),Some(99999999))),
        incomeTax = Some(IncomeTaxModel(Some(1000.25),
          Some(PayPensionsProfitModel(1000.25, 1000.25, List(BandModel(1000.25, 99.99, 1000.25, "ZRT")))),
          Some(DividendsModel(1000.25, 1000.25, Seq(BandModel(1000.25,99.99,1000.25,"HRT")))),
          Some(SavingsAndGainsModel(1000.25, 1000.25, List(BandModel(1000.25,99.99,1000.25,"BRT")))), None)),
        taxableIncome = Some(TaxableIncomeModel(None, Some(IncomeReceivedModel(Some(1000.25), Some(1000.25), Some(1000.25), Some(1000.25))))),
        nic = Some(NicModel(Some(1000.25), Some(1000.25)))
      ))))

  val previousCalculationError = Error(Status.INTERNAL_SERVER_ERROR.toString, "ISE")

  val successResponse: JsValue = Json.parse(
    """{
     |	"calcOutput": {
     |		"calcName": "IncomeTaxCalculator",
     |		"calcVersion": "Version1a",
     |		"calcVersionDate": "2016-01-01",
     |		"calcID": "12345678",
     |		"sourceName": "MDTP",
     |		"sourceRef": "ACKREF0001",
     |		"identifier": "AB10001A",
     |		"year": 2016,
     |		"periodFrom": "2016-01-01",
     |		"periodTo": "2016-01-01",
     |		"calcAmount": 22.56,
     |		"calcTimestamp": "4498-07-06T21:42:24.294Z",
     |		"calcResult": {
     |			"incomeTaxNicYtd": 500.68,
     |			"incomeTaxNicDelta": 1000.25,
     |			"nationalRegime": "UK",
     |			"totalTaxableIncome": 1000.25,
     |			"taxableIncome": {
     |				"totalIncomeReceived": 1000.25,
     |				"incomeReceived": {
     |					"employmentIncome": 1000.25,
     |					"employments": {
     |						"totalPay": 1000.25,
     |						"totalBenefitsAndExpenses": 1000.25,
     |						"totalAllowableExpenses": 1000.25,
     |						"employment": [{
     |							"incomeSourceID": "ABIS10000000001",
     |							"latestDate": "2016-01-01",
     |							"netPay": 1000.25,
     |							"benefitsAndExpenses": 1000.25,
     |							"allowableExpenses": 1000.25
     |						}]
     |					},
     |					"selfEmploymentIncome": 1000.25,
     |					"selfEmployment": [{
     |						"incomeSourceID": "ABIS10000000001",
     |						"latestDate": "2016-01-01",
     |						"accountStartDate": "2016-01-01",
     |						"accountEndDate": "2016-01-01",
     |						"taxableIncome": 1000.25,
     |						"finalised": true
     |					}],
     |					"ukPropertyIncome": 1000.25,
     |					"ukProperty": {
     |						"incomeSourceID": "ABIS10000000001",
     |						"latestDate": "2016-01-01",
     |						"taxableProfit": 1000.25,
     |						"taxableProfitFhlUk": 1000.25,
     |						"finalised": true
     |					},
     |					"bbsiIncome": 1000.25,
     |					"bbsi": {
     |						"incomeSourceID": "ABIS10000000001",
     |						"latestDate": "2016-01-01"
     |					},
     |					"ukDividendIncome": 1000.25,
     |					"ukDividends": {
     |						"incomeSourceID": "ABIS10000000001",
     |						"latestDate": "2016-01-01"
     |					}
     |				},
     |				"totalAllowancesAndDeductions": 1000.25,
     |				"allowancesAndDeductions": {
     |					"giftOfInvestmentsAndPropertyToCharity": 1000.25,
     |					"apportionedPersonalAllowance": 1000.25
     |				}
     |			},
     |			"totalIncomeTax": 1000.25,
     |			"incomeTax": {
     |				"totalBeforeReliefs": 1000.25,
     |				"taxableIncome": 1000.25,
     |				"payPensionsProfit": {
     |					"totalAmount": 1000.25,
     |					"taxableIncome": 1000.25,
     |					"band": [{
     |						"name": "ZRT",
     |						"rate": 99.99,
     |						"threshold": 99999999,
     |						"apportionedThreshold": 99999999,
     |						"income": 1000.25,
     |						"taxAmount": 1000.25
     |					}]
     |				},
     |				"savingsAndGains": {
     |					"totalAmount": 1000.25,
     |					"taxableIncome": 1000.25,
     |					"band": [{
     |						"name": "BRT",
     |						"rate": 99.99,
     |						"threshold": 99999999,
     |						"apportionedThreshold": 99999999,
     |						"income": 1000.25,
     |						"taxAmount": 1000.25
     |					}]
     |				},
     |				"dividends": {
     |					"totalAmount": 1000.25,
     |					"taxableIncome": 1000.25,
     |					"band": [{
     |						"name": "HRT",
     |						"rate": 99.99,
     |						"threshold": 99999999,
     |						"apportionedThreshold": 99999999,
     |						"income": 1000.25,
     |						"taxAmount": 1000.25
     |					}]
     |				},
     |				"totalAfterReliefs": 10.02,
     |				"totalAllowancesAndReliefs": 1000.25,
     |				"allowancesAndReliefs": {
     |					"propertyFinanceRelief": 1000.25
     |				}
     |			},
     |			"totalNic": 1000.25,
     |			"nic": {
     |				"class2": {
     |					"amount": 1000.25,
     |					"weekRate": 1000.25,
     |					"weeks": 1.1,
     |					"limit": 99999999,
     |					"apportionedLimit": 2
     |				},
     |				"class4": {
     |					"totalAmount": 1000.25,
     |					"band": [{
     |						"name": "HRT",
     |						"rate": 99.99,
     |						"threshold": 99999999,
     |						"apportionedThreshold": 99999999,
     |						"income": 1000.25,
     |						"amount": 1000.25
     |					}]
     |				}
     |			},
     |			"eoyEstimate": {
     |				"incomeSource": [{
     |					"id": "abcdefghijklm",
     |					"type": "01",
     |					"taxableIncome": 99999999.99,
     |					"supplied": true,
     |					"finalised": true
     |				}],
     |				"totalTaxableIncome": 99999999.99,
     |				"incomeTaxAmount": 99999999.99,
     |				"nic2": 99999999,
     |				"nic4": 99999999,
     |				"totalNicAmount": 99999999.99,
     |				"incomeTaxNicAmount": 125.63
     |			},
     |			"msgCount": 1.1,
     |			"msg": [{
     |				"type": "abcdefghijklm",
     |				"text": "abcdefghijklm"
     |			}],
     |			"previousCalc": {
     |				"calcTimestamp": "4498-07-06T21:42:24.294Z",
     |				"calcID": "00000000",
     |				"calcAmount": 1000.25
     |			},
     |			"annualAllowances": {
     |				"personalAllowance": 99999999,
     |        "giftAidExtender": 99999999,
     |				"reducedPersonalAllowanceThreshold": 99999999,
     |				"reducedPersonalisedAllowance": 99999999
     |			}
     |		},
     |		"crystallised": true
     |	}
     | }""".stripMargin)


  val failureResponse: (String, String) => JsObject = (code: String, reason: String) =>
    Json.obj(
      "code" -> code,
      "reason" -> reason
    )
}


