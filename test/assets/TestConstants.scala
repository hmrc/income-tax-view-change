/*
 * Copyright 2018 HM Revenue & Customs
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

import models._
import play.api.libs.json.{JsValue, Json}
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse
import java.time.LocalDate


object TestConstants {

  val testNino = "BB123456A"
  val mtdRef = "123456789012345"

  object FinancialData {

    val testYear = "2018"
    val testCalcType = "it"

    val lastTaxCalc = LastTaxCalculation("testCalcId", "testTimestamp", 2345.67)
    val lastTaxCalculationError = LastTaxCalculationError(Status.INTERNAL_SERVER_ERROR, "Error Message")

    //Connector Responses
    val successResponse = HttpResponse(Status.OK, Some(Json.toJson(lastTaxCalc)))
    val badJson = HttpResponse(Status.OK, responseJson = Some(Json.parse("{}")))
    val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, responseString = Some("Error Message"))

  }

  object BusinessDetails {

    val testNinoModel = NinoModel(testNino)

    val testAccountingPeriodModel = AccountingPeriodModel(
      start = LocalDate.parse("2017-06-01"),
      end = LocalDate.parse("2018-05-31")
    )

    val testCessationModel = CessationModel(
      Some(LocalDate.parse("2017-06-01")),
      Some("Dummy reason")
    )

    val testPropertiesRentedModel = PropertiesRentedModel(
      uk = Some(3),
      eea = Some(2),
      nonEea = Some(1),
      total = Some(4)
    )

    val testAddressModel = AddressModel(
      addressLine1 = "Test Lane",
      addressLine2 = Some("Test Unit"),
      addressLine3 = Some("Test Town"),
      addressLine4 = Some("Test City"),
      postCode = Some("TE5 7TE"),
      countryCode = "GB")

    val testMinimumAddressModel = AddressModel(
      addressLine1 = "Test Lane",
      addressLine2 = None,
      addressLine3 = None,
      addressLine4 = None,
      postCode = None,
      countryCode = "GB")

    val testContactDetailsModel = ContactDetailsModel(
      phoneNumber = Some("01332752856"),
      mobileNumber = Some("07782565326"),
      faxNumber = Some("01332754256"),
      emailAddress = Some("stephen@manncorpone.co.uk"))

    val testSomeContactDetailsModel = ContactDetailsModel(None,None,None, emailAddress = Some("stephen@manncorpone.co.uk"))

    val testBusinessDetailsModel =
      BusinessDetailsModel(
        incomeSourceId = "111111111111111",
        accountingPeriod = testAccountingPeriodModel,
        tradingName = Some("Test Business"),
        address = Some(testAddressModel),
        contactDetails = Some(testContactDetailsModel),
        tradingStartDate = Some(LocalDate.parse("2017-01-01")),
        cashOrAccruals = Some("cash"),
        seasonal = Some(true),
        cessation = Some(testCessationModel),
        paperless = Some(true)
      )


    val testMinimumBusinessDetailsModel = BusinessDetailsModel(
      incomeSourceId = "111111111111111",
      accountingPeriod = testAccountingPeriodModel,
      tradingName = None,
      address = None,
      contactDetails = None,
      tradingStartDate = None,
      cashOrAccruals = None,
      seasonal = None,
      cessation = None,
      paperless = None
    )


    val testPropertyDetailsModel = PropertyDetailsModel(
      incomeSourceId = "111111111111111",
      accountingPeriod = testAccountingPeriodModel,
      contactDetails = Some(testSomeContactDetailsModel),
      propertiesRented = Some(testPropertiesRentedModel),
      cessation = Some(testCessationModel),
      paperless = Some(true)
    )

    val testMinimumPropertyDetailsModel = PropertyDetailsModel(
      incomeSourceId = "111111111111111",
      accountingPeriod = testAccountingPeriodModel,
      contactDetails = None,
      propertiesRented = None,
      cessation = None,
      paperless = None
    )

    val testIncomeSourceDetailsModel = IncomeSourceDetailsModel(
      nino = testNino,
      businessData = List(testBusinessDetailsModel,testMinimumBusinessDetailsModel),
      propertyData = Some(testPropertyDetailsModel)
    )

    val testMinimumIncomeSourceDetailsModel = IncomeSourceDetailsModel(
      nino = testNino,
      businessData = List(),
      propertyData = None
    )


    val testPropertiesRentedJson = Json.obj(
      "numPropRented" -> 4,
      "numPropRentedUK" -> 3,
      "numPropRentedEEA" -> 2,
      "numPropRentedNONEEA" -> 1
    )

    val testPropertiesRentedToJson = Json.obj(
      "uk" -> 3,
      "eea" -> 2,
      "nonEea" -> 1,
      "total" -> 4
    )

    val testAccountingPeriodJson = Json.obj(
      "accountingPeriodStartDate"->"2017-06-01",
      "accountingPeriodEndDate"->"2018-05-31"
    )

    val testAccountingPeriodToJson = Json.obj(
      "start"->"2017-06-01",
      "end"->"2018-05-31"
    )

    val testCessationJson = Json.obj(
      "cessationDate" -> "2017-06-01",
      "cessationReason" -> "Dummy reason"
    )

    val testCessationToJson = Json.obj(
      "date" -> "2017-06-01",
      "reason" -> "Dummy reason"
    )

    val testAddressJson = Json.obj(
      "addressLine1"->"Test Lane",
      "addressLine2"->"Test Unit",
      "addressLine3"->"Test Town",
      "addressLine4"->"Test City",
      "postalCode"->"TE5 7TE",
      "countryCode"->"GB"
    )

    val testAddressToJson = Json.obj(
      "addressLine1"->"Test Lane",
      "addressLine2"->"Test Unit",
      "addressLine3"->"Test Town",
      "addressLine4"->"Test City",
      "postCode"->"TE5 7TE",
      "countryCode"->"GB"
    )

    val testMinimumAddressJson = Json.obj(
      "addressLine1"->"Test Lane",
      "countryCode"->"GB"
    )

    val testContactDetailsJson = Json.obj(
      "phoneNumber"->"01332752856",
      "mobileNumber"->"07782565326",
      "faxNumber"->"01332754256",
      "emailAddress"->"stephen@manncorpone.co.uk"
    )

    val testBusinessDetailsJson = Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriodStartDate"->"2017-06-01",
      "accountingPeriodEndDate"->"2018-05-31",
      "tradingName"->"Test Business",
      "businessAddressDetails" -> testAddressJson,
      "businessContactDetails" -> testContactDetailsJson,
      "tradingStartDate"->"2017-01-01",
      "cashOrAccruals"->"cash",
      "seasonal" -> true,
      "cessationDate" -> "2017-06-01",
      "cessationReason" -> "Dummy reason",
      "paperless" -> true
    )

    val testBusinessDetailsToJson = Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriod"-> testAccountingPeriodToJson,
      "tradingName"->"Test Business",
      "address" -> testAddressToJson,
      "contactDetails" -> testContactDetailsJson,
      "tradingStartDate"->"2017-01-01",
      "cashOrAccruals"->"cash",
      "seasonal" -> true,
      "cessation" -> testCessationToJson,
      "paperless" -> true
    )

    val testMinimumBusinessDetailsJson = Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriodStartDate"->"2017-06-01",
      "accountingPeriodEndDate"->"2018-05-31"
    )

    val testPropertyDetailsJson = Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriodStartDate"->"2017-06-01",
      "accountingPeriodEndDate"->"2018-05-31",
      "emailAddress" -> "stephen@manncorpone.co.uk",
      "numPropRentedUK" -> 3,
      "numPropRentedEEA" -> 2,
      "numPropRentedNONEEA" -> 1,
      "numPropRented" -> 4,
      "cessationDate" -> "2017-06-01",
      "cessationReason" -> "Dummy reason",
      "paperless" -> true
    )

    val testMinimumBusinessDetailsToJson = Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriod"-> testAccountingPeriodToJson
    )

    val testPropertyDetailsToJson = Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriod" -> testAccountingPeriodToJson,
      "contactDetails" -> Json.obj(
        "emailAddress" -> "stephen@manncorpone.co.uk"
      ),
      "propertiesRented" -> testPropertiesRentedToJson,
      "cessation" -> testCessationToJson,
      "paperless" -> true
    )

    val testMinimumPropertyDetailsJson = Json.obj(
      "incomeSourceId" -> "111111111111111",
      "accountingPeriodStartDate"->"2017-06-01",
      "accountingPeriodEndDate"->"2018-05-31"
    )

    val testIncomeSourceDetailsJson = Json.obj(
      "safeId" -> "XAIT12345678908",
      "nino" -> testNino,
      "mtdbsa" -> mtdRef,
      "businessData" -> Json.arr(testBusinessDetailsJson,testMinimumBusinessDetailsJson),
      "propertyData" -> testPropertyDetailsJson
    )

    val testIncomeSourceDetailsToJson = Json.obj(
      "nino" -> testNino,
      "businessData" -> Json.arr(
        testBusinessDetailsToJson,
        testMinimumBusinessDetailsToJson),
      "propertyData" -> testPropertyDetailsToJson
    )

    val testMinimumIncomeSourceDetailsJson = Json.obj(
      "nino" -> testNino
    )

    val testIncomeSourceDetailsError = IncomeSourceDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")

    //Connector Responses
    val successResponse = HttpResponse(Status.OK, Some(testIncomeSourceDetailsJson))
    val badJson = HttpResponse(Status.OK, Some(Json.toJson("{}")))
    val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, responseString = Some("Dummy error message"))
  }
}
