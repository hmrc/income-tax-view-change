
package controllers

import assets.BaseIntegrationTestConstants._
import assets.LastTaxCalcIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.FinancialDataStub
import models.latestTaxCalculation.LastTaxCalculation
import play.api.http.Status._

class EstimatedTaxLiabilityControllerISpec extends ComponentSpecBase {
  "Calling the EstimatedTaxLiabilityController" when {
    "authorised with a valid request" should {
      "return a valid simple calculation estimate" in {

        isAuthorised(true)

        And("I wiremock stub a successful Get Financial Data response")
        FinancialDataStub.stubGetFinancialData(testNino, testYear, testCalcType, lastTaxCalculation)

        When(s"I call GET /income-tax-view-change/estimated-tax-liability/$testNino/$testYear/$testCalcType")
        val res = IncomeTaxViewChange.getEstimatedTaxLiability(testNino, testYear, testCalcType)

        FinancialDataStub.verifyGetFinancialData(testNino, testYear, testCalcType)

        Then("a successful response is returned with the correct estimate")

        res should have(
          httpStatus(OK),
          jsonBodyAs[LastTaxCalculation](lastTaxCalculation)
        )
      }
    }
    "unauthorised" should {
      "return an error" in {

        isAuthorised(false)

        When(s"I call GET /income-tax-view-change/estimated-tax-liability/$testNino/$testYear")
        val res = IncomeTaxViewChange.getEstimatedTaxLiability(testNino, testYear, testCalcType)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }
}
