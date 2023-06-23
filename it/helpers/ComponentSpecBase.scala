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

package helpers

import helpers.servicemocks.AuthStub
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsValue
import play.api.libs.ws.WSResponse
import play.api.{Application, Environment, Mode}

import java.time.LocalDate

trait ComponentSpecBase extends TestSuite with CustomMatchers
  with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience with Matchers
  with WiremockHelper with BeforeAndAfterEach with BeforeAndAfterAll with Eventually {

  val mockHost = WiremockHelper.wiremockHost
  val mockPort = WiremockHelper.wiremockPort.toString
  val mockUrl = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.if.url" -> mockUrl,
    "microservice.services.des.url" -> mockUrl
  )

  def configWIthSubmissionStub: Map[String, String] = Map(
    "microservice.services.auth.host" -> mockHost,
    "microservice.services.auth.port" -> mockPort,
    "microservice.services.if.url" -> mockUrl,
    "microservice.services.des.url" -> mockUrl,
    "submissionStubUrl" -> mockUrl,
    "useBusinessDetailsStub" -> "true",
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .build()

  implicit lazy val appWithSubmissionStub: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(configWIthSubmissionStub)
    .build()


  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  def isAuthorised(authorised: Boolean): Unit = {
    if (authorised) {
      Given("I wiremock stub an authorised user response")
      AuthStub.stubAuthorised()
    } else {
      Given("I wiremock stub an unauthorised user response")
      AuthStub.stubUnauthorised()
    }
  }

  object IncomeTaxViewChange {
    def getPaymentAllocations(nino: String, paymentLot: String, paymentLotItem: String): WSResponse = {
      get(s"/$nino/payment-allocations/$paymentLot/$paymentLotItem")
    }

    def get(uri: String): WSResponse = buildClient(uri).get().futureValue

    def getChargeDetails(nino: String, from: String, to: String): WSResponse = {
      get(s"/$nino/financial-details/charges/from/$from/to/$to")
    }

    def getPaymentAllocationDetails(nino: String, documentId: String): WSResponse = {
      get(s"/$nino/financial-details/charges/documentId/$documentId")
    }

    def getOnlyOpenItems(nino: String): WSResponse = {
      get(s"/$nino/financial-details/only-open-items")
    }

    def getChargeHistory(mtdBsa: String, documentId: String): WSResponse = {
      get(s"/charge-history/$mtdBsa/docId/$documentId")
    }

    def getPaymentDetails(nino: String, from: String, to: String): WSResponse = {
      get(s"/$nino/financial-details/payments/from/$from/to/$to")
    }

    def getPreviousCalculation(nino: String, year: String): WSResponse = get(s"/previous-tax-calculation/$nino/$year")

    def getCalculationList(nino: String, taxYear: String): WSResponse = get(s"/list-of-calculation-results/$nino/$taxYear")

    def getCalculationList2324(nino: String, taxYear: String): WSResponse = get(s"/calculation-list/$nino/$taxYear")

    def getNino(mtdRef: String): WSResponse = get(s"/nino-lookup/$mtdRef")

    def getIncomeSources(mtdRef: String): WSResponse = get(s"/income-sources/$mtdRef")

    def getBusinessDetails(nino: String): WSResponse = get(s"/get-business-details/nino/$nino")

    def getReportDeadlines(nino: String): WSResponse = get(s"/$nino/report-deadlines")

    def getPreviousObligations(nino: String, from: String, to: String): WSResponse = get(s"/$nino/fulfilled-report-deadlines/from/$from/to/$to")

    def getFulfilledReportDeadlines(nino: String): WSResponse = get(s"/$nino/fulfilled-report-deadlines")

    def getOutStandingChargeDetails(idType: String, idNumber: String, taxYearEndDate: String): WSResponse = get(s"/out-standing-charges/$idType/$idNumber/$taxYearEndDate")

    def getRepaymentHistoryById(nino: String, repaymentId: String): WSResponse = {
      get(s"/repayments/$nino/repaymentId/$repaymentId")
    }

    def getAllRepaymentHistory(nino: String): WSResponse = {
      get(s"/repayments/$nino")
    }

    def putUpdateCessationDate(body: JsValue): WSResponse = {
      buildClient("/update-income-source/update-cessation-date").put(body).futureValue
    }

  }
}
