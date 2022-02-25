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

package assets

import models.PreviousCalculation._
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}

object PreviousCalculationTestConstants {

  val testNino: String = "AA000000B"
  val testYear: String = "2008"

  val responseJsonFullWithoutExtra: JsValue = Json.parse(
    """
      |{
      |  "calcOutput": {
      |    "calcID": "1",
      |    "calcAmount": 22.56,
      |    "calcTimestamp": "2008-05-01",
      |    "crystallised": true,
      |    "calcResult": {
      |      "incomeTaxNicYtd": 500.68,
      |      "eoyEstimate": {
      |        "incomeTaxNicAmount": 125.63
      |      },
      |      "nationalRegime": "Scotland",
      |      "totalTaxableIncome": 198500,
      |      "annualAllowances": {
      |        "personalAllowance": 11500
      |      },
      |      "incomeTax": {
      |        "totalAllowancesAndReliefs": 1000,
      |        "payPensionsProfit": {
      |          "totalAmount": 66500,
      |          "taxableIncome": 170000,
      |          "band": [
      |            {
      |              "income": 20000,
      |              "rate": 20,
      |              "taxAmount": 4000,
      |              "name": "BRT"
      |            },
      |            {
      |              "income": 100000,
      |              "rate": 40,
      |              "taxAmount": 40000,
      |              "name": "HRT"
      |            },
      |            {
      |              "income": 50000,
      |              "rate": 45,
      |              "taxAmount": 22500,
      |              "name": "ART"
      |            }
      |          ]
      |        },
      |        "dividends": {
      |          "totalAmount": 1968,
      |          "taxableIncome": 5000,
      |          "band": [
      |            {
      |              "income": 1000,
      |              "rate": 7.5,
      |              "taxAmount": 75,
      |              "name": "basic-band"
      |            },
      |            {
      |              "income": 2000,
      |              "rate": 37.5,
      |              "taxAmount": 750,
      |              "name": "higher-band"
      |            },
      |            {
      |              "income": 3000,
      |              "rate": 38.1,
      |              "taxAmount": 1143,
      |              "name": "additional-band"
      |            }
      |          ]
      |        },
      |        "savingsAndGains": {
      |          "totalAmount": 815.55,
      |          "taxableIncome": 1979,
      |          "band": [
      |            {
      |              "income": 1,
      |              "rate": 0,
      |              "taxAmount": 0,
      |              "name": "SSR"
      |            },
      |            {
      |              "income": 20,
      |              "rate": 0,
      |              "taxAmount": 0,
      |              "name": "ZRT"
      |            },
      |            {
      |              "income": 500,
      |              "rate": 20,
      |              "taxAmount": 100,
      |              "name": "BRT"
      |            },
      |            {
      |              "income": 1000,
      |              "rate": 40,
      |              "taxAmount": 400,
      |              "name": "HRT"
      |            },
      |            {
      |              "income": 479,
      |              "rate": 45,
      |              "taxAmount": 215.55,
      |              "name": "ART"
      |            }
      |          ]
      |        },
      |        "giftAid": {
      |          "paymentsMade": 150,
      |          "rate": 0,
      |          "taxableAmount": 0
      |        }
      |      },
      |      "taxableIncome": {
      |        "totalIncomeAllowancesUsed": 12005,
      |        "allowancesAndDeductions": {
      |          "giftOfInvestmentsAndPropertyToCharity": 1000.25
      |        },
      |        "incomeReceived": {
      |          "selfEmploymentIncome": 200000,
      |          "ukPropertyIncome": 10000,
      |          "bbsiIncome": 2000,
      |          "ukDividendIncome": 11000
      |        }
      |      },
      |      "nic": {
      |        "class2": {
      |          "amount": 10000
      |        },
      |        "class4": {
      |          "totalAmount": 14000
      |        }
      |      }
      |    }
      |  }
      |}
    """.stripMargin)

  val responseJsonFull: JsValue = Json.parse(
    """
      |{"calcOutput":{
      |"calcID":"1",
      |"calcAmount":22.56,
      |"calcTimestamp":"2008-05-01",
      |"crystallised":true,
      |
      |        "calcSummary" : {
      |          "nationalRegime" : "UK",
      |          "incomeTaxGross" : 68985412739.5,
      |          "taxDeducted" : 33971782272.57,
      |          "incomeTaxNetOfDeductions" : 39426248386.69,
      |          "nic2Gross" : 10000,
      |          "nic4Gross" : 14000,
      |          "nic2NetOfDeductions" : 89311246978.32,
      |          "nic4NetOfDeductions" : 193784559071.9
      |        },
      |        "calcResult" : {
      |          "incomeTaxNicYtd" : 500.68,
      |          "nationalRegime" : "Scotland",
      |          "totalTaxableIncome" : 198500,
      |          "totalNic" : 180000,
      |          "nic" : {
      |            "class2" : {
      |              "amount" : 10000,
      |              "weekRate" : 2.95,
      |              "weeks" : 13,
      |              "limit" : 6205,
      |              "apportionedLimit" : 1547
      |            },
      |            "class4" : {
      |              "totalAmount" : 14000,
      |              "band" : [
      |                {
      |                  "name" : "ZRT",
      |                  "rate" : 0,
      |                  "threshold" : 8424,
      |                  "apportionedThreshold" : 2101,
      |                  "income" : 2101,
      |                  "amount" : 0
      |                },
      |                {
      |                  "name" : "BRT",
      |                  "rate" : 9,
      |                  "threshold" : 46350,
      |                  "apportionedThreshold" : 11556,
      |                  "income" : 3096,
      |                  "amount" : 278.64
      |                }
      |              ]
      |            }
      |          },
      |          "totalBeforeTaxDeducted" : 100,
      |          "totalTaxDeducted" : 200,
      |          "annualAllowances" : {
      |            "personalAllowance" : 11500
      |          },
      |          "incomeTax" : {
      |            "totalAllowancesAndReliefs" : 1000,
      |            "giftAid" : {
      |              "paymentsMade" : 150,
      |              "rate" : 0,
      |              "taxableAmount" : 0
      |            },
      |            "payPensionsProfit" : {
      |              "totalAmount" : 66500.00,
      |              "taxableIncome" : 170000.00,
      |              "band" : [{
      |                "name" : "BRT",
      |                "rate" : 20.0,
      |                "income" : 20000.00,
      |                "taxAmount" : 4000.00
      |              }, {
      |                "name" : "HRT",
      |                "rate" : 40.0,
      |                "income" : 100000.00,
      |                "taxAmount" : 40000.00
      |              }, {
      |                "name" : "ART",
      |                "rate" : 45.0,
      |                "income" : 50000.00,
      |                "taxAmount" : 22500.00
      |              }
      |              ]},
      |            "savingsAndGains" : {
      |              "totalAmount" : 815.55,
      |              "taxableIncome" : 1979.00,
      |              "band" : [
      |                {
      |                  "name" : "SSR",
      |                  "rate" : 0,
      |                  "income" : 1,
      |                  "taxAmount" : 0,
      |                  "threshold" : 4000,
      |                  "apportionedThreshold" : 5000
      |                },
      |                {
      |                  "name" : "ZRT",
      |                  "rate" : 0,
      |                  "income" : 20,
      |                  "taxAmount" : 0,
      |                  "threshold" : 1,
      |                  "apportionedThreshold" : 1
      |                },
      |                {
      |                  "name" : "BRT",
      |                  "rate" : 20,
      |                  "income" : 500,
      |                  "taxAmount" : 100,
      |                  "threshold" : 1,
      |                  "apportionedThreshold" : 1
      |                },
      |                {
      |                  "name" : "HRT",
      |                  "rate" : 40,
      |                  "income" : 1000,
      |                  "taxAmount" : 400,
      |                  "threshold" : 1,
      |                  "apportionedThreshold" : 1
      |                },
      |                {
      |                  "name" : "ART",
      |                  "rate" : 45,
      |                  "income" : 479,
      |                  "taxAmount" : 215.55,
      |                  "threshold" : 1,
      |                  "apportionedThreshold" : 1
      |                }
      |              ],
      |              "personalAllowanceUsed" : 15487995938.37
      |            },
      |            "dividends" : {
      |              "totalAmount" : 1968.00,
      |              "taxableIncome" : 5000.00,
      |              "band" : [
      |                {
      |                  "name" : "basic-band",
      |                  "rate" : 7.5,
      |                  "income" : 1000,
      |                  "taxAmount" : 75.0
      |                },
      |                {
      |                  "name" : "higher-band",
      |                  "rate" : 37.5,
      |                  "income" : 2000,
      |                  "taxAmount" : 750
      |                },
      |                {
      |                  "name" : "additional-band",
      |                  "rate" : 38.1,
      |                  "income" : 3000,
      |                  "taxAmount" : 1143
      |                }]}
      |          },
      |          "taxableIncome" : {
      |            "allowancesAndDeductions": {
      |               "giftOfInvestmentsAndPropertyToCharity": 1000.25
      |            },
      |            "totalIncomeAllowancesUsed" : 12005,
      |            "incomeReceived" : {
      |              "employmentIncome" : 100,
      |              "selfEmploymentIncome" : 200000,
      |              "ukPropertyIncome" : 10000,
      |              "bbsiIncome" : 2000,
      |              "ukDividendIncome" : 11000,
      |              "employments": {
      |                "totalPay" : 55000961025.98,
      |                "totalBenefitsAndExpenses": 96945498573.96,
      |                "totalAllowableExpenses": 94037790451.1,
      |                "employment": [
      |                  {
      |                    "incomeSourceID": "33j38jIEnKNa5aV",
      |                    "latestDate": "3661-09-02",
      |                    "netPay": 57775446337.53,
      |                    "benefitsAndExpenses": 25047077371.97,
      |                    "allowableExpenses": 3585774590.1800003
      |                  }
      |                ]
      |              },
      |              "selfEmployment" : [
      |                {
      |                  "incomeSourceID" : "BcjTLlMBb3vlAne",
      |                  "latestDate" : "8225-09-22",
      |                  "taxableIncome" : 60455823926.5,
      |                  "accountStartDate" : "9571-09-26",
      |                  "accountEndDate" : "5906-07-06",
      |                  "finalised" : false,
      |                  "losses" : 56154428355.74
      |                },
      |                {
      |                  "incomeSourceID" : "v4wly6Tn5JfwLjB",
      |                  "latestDate" : "5217-10-10",
      |                  "taxableIncome" : 82204159598.88,
      |                  "accountStartDate" : "5688-03-30",
      |                  "accountEndDate" : "6756-05-09",
      |                  "finalised" : true,
      |                  "losses" : 16496201041.710001
      |                }
      |              ],
      |              "ukProperty" : {
      |                "incomeSourceID" : "Q9wFE164KgzVR2m",
      |                "latestDate" : "0379-03-30",
      |                "taxableProfit" : 60297189257.64,
      |                "taxableProfitFhlUk" : 7347733383.54,
      |                "finalised" : false,
      |                "losses" : 4549677842.09,
      |                "lossesFhlUk" : 79888527010.89
      |              },
      |              "bbsi" : {
      |                "totalTaxedInterestIncome" : 66480042461.21,
      |                "taxedAccounts" : [
      |                  {
      |                    "incomeSourceID" : "yysKzVIfqcLWVuQ",
      |                    "latestDate" : "7650-11-26",
      |                    "name" : "eiusmod Ut et dolore deserunt",
      |                    "gross" : 10513891004.58,
      |                    "net" : 63946537010.58,
      |                    "taxDeducted" : 32104251608.440002
      |                  }
      |                ]
      |              },
      |              "ukDividend" : {
      |                "ukDividends" : 7549829503.03,
      |                "otherUkDividends" : 34590087015.69
      |              }
      |            }
      |          },
      |          "eoyEstimate" : {
      |            "totalTaxableIncome" : 198500,
      |            "incomeTaxAmount" : 89999999.99,
      |            "nic2" : 89999999.99,
      |            "nic4" : 89999999.99,
      |            "totalNicAmount" : 66000,
      |            "incomeTaxNicAmount" : 125.63,
      |            "selfEmployment" : [
      |              {
      |                "id" : "selfEmploymentId1",
      |                "taxableIncome" : 89999999.99,
      |                "supplied" : true,
      |                "finalised" : true
      |              },
      |              {
      |                "id" : "selfEmploymentId2",
      |                "taxableIncome" : 89999999.99,
      |                "supplied" : true,
      |                "finalised" : true
      |              }
      |            ],
      |            "ukProperty" : [
      |              {
      |                "taxableIncome" : 89999999.99,
      |                "supplied" : true,
      |                "finalised" : true
      |              }
      |            ]
      |          }
      |        }
      |}}
    """.stripMargin
  )

  val previousCalculationFull: PreviousCalculationModel =
    PreviousCalculationModel(
      CalcOutput(calcID = "1", calcAmount = Some(22.56), calcTimestamp = Some("2008-05-01"), crystallised = Some(true),
        calcResult = Some(CalcResult(
          incomeTaxNicYtd = 500.68,
          eoyEstimate = Some(EoyEstimate(125.63)),
          nationalRegime = Some("Scotland"),
          totalTaxableIncome = Some(198500),
          annualAllowances = Some(AnnualAllowancesModel(
            personalAllowance = Some(11500.00)
          )),
          incomeTax = Some(IncomeTaxModel(
            totalAllowancesAndReliefs = Some(1000),
            payAndPensionsProfit = Some(PayPensionsProfitModel(
              totalAmount = 66500,
              taxableIncome = 170000,
              List(
                BandModel(20000.00, 20.0, 4000.00, "BRT"),
                BandModel(100000.00, 40.0, 40000.00, "HRT"),
                BandModel(50000.00, 45.0, 22500.00, "ART")
              ))),
            dividends = Some(DividendsModel(
              totalAmount = 1968,
              taxableIncome = 5000,
              Seq(
                BandModel(
                  1000.0,
                  7.5,
                  75.0,
                  name = "basic-band"
                ),
                BandModel(
                  2000.0,
                  37.5,
                  750.0,
                  "higher-band"
                ),
                BandModel(
                  3000.0,
                  38.1,
                  1143.0,
                  "additional-band"
                )
              )
            )),
            savingsAndGains = Some(SavingsAndGainsModel(
              totalAmount = 815.55,
              taxableIncome = 1979,
              Seq(BandModel(
                income = 1.00,
                rate = 0.0,
                taxAmount = 0.0,
                name = "SSR"
              ),
                BandModel(
                  income = 20.00,
                  rate = 0.0,
                  taxAmount = 0.0,
                  name = "ZRT"
                ),
                BandModel(
                  income = 500.0,
                  rate = 20.0,
                  taxAmount = 100.0,
                  name = "BRT"
                ),
                BandModel(
                  income = 1000.0,
                  rate = 40.0,
                  taxAmount = 400.0,
                  name = "HRT"
                ),
                BandModel(
                  income = 479.0,
                  rate = 45.0,
                  taxAmount = 215.55,
                  name = "ART"
                ))
            )),
            giftAid = Some(GiftAidModel(
              paymentsMade = 150,
              rate = 0,
              taxableAmount = 0
            ))
          )),
          taxableIncome = Some(TaxableIncomeModel(
            totalIncomeAllowancesUsed = Some(12005.00),
            incomeReceived = Some(IncomeReceivedModel(
              selfEmploymentIncome = Some(200000.00),
              ukPropertyIncome = Some(10000.00),
              bbsiIncome = Some(2000.00),
              ukDividendIncome = Some(11000.00)
            )),
            giftOfInvestmentsAndPropertyToCharity = Some(1000.25)
          )),
          nic = Some(NicModel(Some(10000), Some(14000)))
        ))))

  val previousCalculationMinimum: PreviousCalculationModel =
    PreviousCalculationModel(
      CalcOutput(calcID = "1", calcAmount = None, calcTimestamp = None, crystallised = None,
        calcResult = None))

  val responseJsonMinimum: JsValue = Json.parse(
    """{"calcOutput": {"calcID": "1"}}""".stripMargin)

  val testPreviousCalculationNoEoy: PreviousCalculationModel =
    PreviousCalculationModel(
      CalcOutput(calcID = "1", calcAmount = Some(22.56), calcTimestamp = Some("2008-05-01"), crystallised = Some(true),
        calcResult = Some(CalcResult(incomeTaxNicYtd = 500.68, eoyEstimate = None))))

  val responseJsonNoEoy: JsValue = Json.parse(
    """{
      |	"calcOutput": {
      |		"calcID": "1",
      |		"calcAmount": 22.56,
      |		"calcTimestamp": "2008-05-01",
      |		"crystallised": true,
      |		"calcResult": {
      |			"incomeTaxNicYtd": 500.68
      |		}
      |	}
      |}""".stripMargin)

  val jsonMultipleErrors: JsValue =
    Json.obj(
      "failures" -> Json.arr(
        Json.obj(
          "code" -> "ERROR CODE 1",
          "reason" -> "ERROR MESSAGE 1"
        ),
        Json.obj(
          "code" -> "ERROR CODE 2",
          "reason" -> "ERROR MESSAGE 2"
        )
      )
    )

  val multiError = MultiError(
    failures = Seq(
      Error(code = "ERROR CODE 1", reason = "ERROR MESSAGE 1"),
      Error(code = "ERROR CODE 2", reason = "ERROR MESSAGE 2")
    )
  )

  val singleError = Error(code = "CODE", reason = "ERROR MESSAGE")

  val jsonSingleError: JsValue = Json.obj("code" -> "CODE", "reason" -> "ERROR MESSAGE")

  val badRequestMultiError: Either[ErrorResponse, Nothing] = Left(ErrorResponse(
    Status.BAD_REQUEST,
    multiError
  ))

  val badRequestSingleError: Either[ErrorResponse, Nothing] =
    Left(ErrorResponse(Status.BAD_REQUEST, singleError))

  val badNino: String = "55F"
}
