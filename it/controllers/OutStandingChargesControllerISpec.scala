
package controllers

import assets.OutStandingChargesIntegrationTestConstant._
import helpers.ComponentSpecBase
import helpers.servicemocks.DesOutStandingChargesStub._
import models.outStandingCharges.OutstandingChargesSuccessResponse
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse

class OutStandingChargesControllerISpec extends ComponentSpecBase {

  val idType: String = "utr"
  val idNumber = "1234567890"
  val taxYearEndDate: String = "2020-04-05"

  s"GET ${controllers.routes.OutStandingChargesController.listOutStandingCharges(idType, idNumber, taxYearEndDate)}" should {
    s"return $OK" when {
      "charge details are successfully retrieved" in {

        isAuthorised(true)

        stubGetOutStandingChargeDetails(idType, idNumber, taxYearEndDate)(
          status = OK,
          response = validMultipleOutStandingChargeDesResponseJson)

        val res: WSResponse = IncomeTaxViewChange.getOutStandingChargeDetails("utr", idNumber, taxYearEndDate)

        res should have(
          httpStatus(OK),
          jsonBodyMatching(Json.toJson(OutstandingChargesSuccessResponse(List(outStandingChargeModelOne, outStandingChargeModelTwo))))
        )
      }
    }

    s"return $NOT_FOUND" when {
      "an unexpected status with NOT_FOUND was returned when retrieving charge details" in {

        isAuthorised(true)

        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        stubGetOutStandingChargeDetails(idType, idNumber, taxYearEndDate)(
          status = NOT_FOUND, response = errorJson
        )

        val res: WSResponse = IncomeTaxViewChange.getOutStandingChargeDetails(idType, idNumber, taxYearEndDate)

        res should have(
          httpStatus(NOT_FOUND),
          bodyMatching(errorJson.toString())
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "an unexpected status was returned when retrieving charge details" in {

        isAuthorised(true)

        stubGetOutStandingChargeDetails(idType, idNumber, taxYearEndDate)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getOutStandingChargeDetails(idType, idNumber, taxYearEndDate)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }

}
