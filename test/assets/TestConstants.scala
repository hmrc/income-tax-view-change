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

    val testNinoModel = Nino(testNino)

    val testBusinessModel = Some(List(
      BusinessData(
        incomeSourceId = "111111111111111",
        accountingPeriodStartDate = "2017-06-01",
        accountingPeriodEndDate = "2018-05-31",
        tradingName = Some("Test Business"),
        businessAddressDetails = Some(
          BusinessAddress(
            addressLine1 = "Test Lane",
            addressLine2 = Some("Test Unit"),
            addressLine3 = Some("Test Town"),
            addressLine4 = Some("Test City"),
            postalCode = "TE5 7TE",
            countryCode = "GB"
          )),
        businessContactDetails = Some(
          BusinessContact(
            phoneNumber = Some("01332752856"),
            mobileNumber = Some("07782565326"),
            faxNumber = Some("01332754256"),
            emailAddress = Some("stephen@manncorpone.co.uk")
          )),
        tradingStartDate = Some("2017-01-01"),
        cashOrAccruals = Some("cash"),
        cessationDate = None,
        cessationReason = None,
        seasonal = Some(true),
        paperless = Some(true)
      )))

    val testMinimumBusinessModel = Some(List(

      BusinessData(
        incomeSourceId = "111111111111111",
        accountingPeriodStartDate = "2017-06-01",
        accountingPeriodEndDate = "2018-05-31",
        tradingName = None,
        businessAddressDetails = None,
        businessContactDetails = None,
        tradingStartDate = None,
        cashOrAccruals = None,
        cessationDate = None,
        cessationReason = None,
        seasonal = None,
        paperless = None
      )
    ))

    def desBusinessResponse(businessModel: Option[List[BusinessData]]): DesBusinessDetails =
      DesBusinessDetails(
        safeId = "XAIT12345678908",
        nino = testNino,
        mtdbsa = mtdRef,
        propertyIncome = Some(false),
        businessData = businessModel,
        propertyData = None
      )

    val testBusinessModelJson: JsValue = Json.parse(
      s"""{
        |"safeId":"XAIT12345678908",
        |"nino":"$testNino",
        |"mtdbsa":"$mtdRef",
        |"propertyIncome":false,
        |"businessData": [
        | {
        |   "incomeSourceId":"111111111111111",
        |   "accountingPeriodStartDate":"2017-06-01",
        |   "accountingPeriodEndDate":"2018-05-31",
        |   "tradingName":"Test Business",
        |   "businessAddressDetails":{
        |     "addressLine1":"Test Lane",
        |     "addressLine2":"Test Unit",
        |     "addressLine3":"Test Town",
        |     "addressLine4":"Test City",
        |     "postalCode":"TE5 7TE",
        |     "countryCode":"GB"
        |   },
        |   "businessContactDetails":{
        |     "phoneNumber":"01332752856",
        |     "mobileNumber":"07782565326",
        |     "faxNumber":"01332754256",
        |     "emailAddress":"stephen@manncorpone.co.uk"
        |   },
        |   "tradingStartDate":"2017-01-01",
        |   "cashOrAccruals":"cash",
        |   "seasonal":true,
        |   "paperless":true
        |  }
        | ]
        |}
      """.stripMargin
    )

    val testDesResponseError = DesBusinessDetailsError(Status.INTERNAL_SERVER_ERROR, "Dummy error message")

    //Connector Responses
    val successResponse = HttpResponse(Status.OK, Some(Json.toJson(desBusinessResponse(testBusinessModel))))
    val badJson = HttpResponse(Status.OK, Some(Json.toJson("{}")))
    val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, responseString = Some("Dummy error message"))
  }
}
