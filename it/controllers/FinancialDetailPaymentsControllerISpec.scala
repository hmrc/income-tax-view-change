
package controllers

import assets.BaseIntegrationTestConstants._
import helpers.ComponentSpecBase
import helpers.servicemocks.DesChargesStub.stubGetChargeDetails
import models.financialDetails.Payment
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSResponse

class FinancialDetailPaymentsControllerDESISpec extends FinancialDetailPaymentsControllerISpec(enableIF = false)

class FinancialDetailPaymentsControllerIFISpec extends FinancialDetailPaymentsControllerISpec(enableIF = true)


abstract class FinancialDetailPaymentsControllerISpec(enableIF: Boolean) extends ComponentSpecBase {

  override def config: Map[String, String] = super.config + ("microservice.services.if.enabled" -> enableIF.toString)

  val from: String = "from"
  val to: String = "to"

  val payments1: Payment = Payment(
    reference = Some("paymentReference"),
    amount = Some(BigDecimal("300.00")),
    method = Some("paymentMethod"),
    lot = Some("lot01"),
    lotItem = Some("0001"),
    date = Some("dueDate"),
		transactionId = "id"
  )

  val payments2: Payment = Payment(
    reference = Some("paymentReference2"),
    amount = Some(BigDecimal("100.00")),
    method = Some("paymentMethod2"),
    lot = Some("lot02"),
    lotItem = Some("0001"),
    date = Some("dueDate2"),
		transactionId = "id2"
  )

  val chargeJson: JsObject = Json.obj(
    "balanceDetails" -> Json.obj(
      "balanceDueWithin30Days" -> 100.00,
      "overDueAmount" -> 200.00,
      "totalBalance" -> 300.00
    ),
    "documentDetails" -> Json.arr(
      Json.obj(
        "taxYear" -> "2018",
        "documentId" -> "id",
        "documentDescription" -> "documentDescription",
        "documentText" -> "documentText",
        "totalAmount" -> 300.00,
        "documentOutstandingAmount" -> 200.00,
				"documentDate" -> "2018-03-29",
				"paymentLot" -> "lot01",
				"paymentLotItem" -> "0001"
      ),
      Json.obj(
        "taxYear" -> "2019",
        "documentId" -> "id2",
        "documentDescription" -> "documentDescription2",
        "documentText" -> "documentText2",
        "totalAmount" -> 100.00,
        "documentOutstandingAmount" -> 50.00,
				"documentDate" -> "2018-03-29",
				"paymentLot" -> "lot02",
				"paymentLotItem" -> "0001"
      )
    ),
    "financialDetails" -> Json.arr(
      Json.obj(
        "taxYear" -> "2018",
        "documentId" -> "id",
        "documentDate" -> "transactionDate",
        "documentDescription" -> "type",
        "originalAmount" -> 1000.00,
        "totalAmount" -> 1000.00,
        "originalAmount" -> 500.00,
        "documentOutstandingAmount" -> 500.00,
        "items" -> Json.arr(
          Json.obj(
            "subItem" -> "1",
            "amount" -> 100.00,
            "clearingDate" -> "clearingDate",
            "clearingReason" -> "clearingReason",
            "outgoingPaymentMethod" -> "outgoingPaymentMethod",
            "paymentReference" -> "paymentReference",
            "paymentAmount" -> 2000.00,
            "dueDate" -> "dueDate",
            "paymentMethod" -> "paymentMethod",
						"paymentLot" -> "lot01",
						"paymentLotItem" -> "0001"
          )
        )
      ),
      Json.obj(
        "taxYear" -> "2019",
        "documentId" -> "id2",
        "documentDate" -> "transactionDate2",
        "documentDescription" -> "type2",
        "totalAmount" -> 2000.00,
        "originalAmount" -> 500.00,
        "documentOutstandingAmount" -> 200.00,
        "items" -> Json.arr(
          Json.obj(
            "subItem" -> "2",
            "amount" -> 200.00,
            "clearingDate" -> "clearingDate2",
            "clearingReason" -> "clearingReason2",
            "outgoingPaymentMethod" -> "outgoingPaymentMethod2",
            "paymentReference" ->"paymentReference2",
            "paymentAmount" -> 3000.00,
            "dueDate" -> "dueDate2",
            "paymentMethod" -> "paymentMethod2",
						"paymentLot" -> "lot02",
						"paymentLotItem" -> "0001"
          )
        )
      )
    )
  )


  s"GET ${controllers.routes.FinancialDetailPaymentsController.getPaymentDetails(testNino, from, to)}" should {
    s"return $OK" when {
      "payment details are successfully retrieved" in {
        isAuthorised(true)

        stubGetChargeDetails(testNino, from, to)(
          status = OK,
          response = chargeJson)

        val res: WSResponse = IncomeTaxViewChange.getPaymentDetails(testNino, from, to)

        res should have(
          httpStatus(OK),
          jsonBodyMatching(Json.toJson(List(payments1, payments2)))
        )
      }
    }

    s"return $NOT_FOUND" when {
      "an unexpected status with NOT_FOUND was returned when retrieving payment details" in {

        isAuthorised(true)

        val errorJson = Json.obj("code" -> "NO_DATA_FOUND", "reason" -> "The remote endpoint has indicated that no data can be found.")
        stubGetChargeDetails(testNino, from, to)(
          status = NOT_FOUND, response = errorJson
        )

        val res: WSResponse = IncomeTaxViewChange.getPaymentDetails(testNino, from, to)

        res should have(
          httpStatus(NOT_FOUND),
          bodyMatching(errorJson.toString())
        )
      }
    }

    s"return $INTERNAL_SERVER_ERROR" when {
      "an unexpected status was returned when retrieving payment details" in {

        isAuthorised(true)

        stubGetChargeDetails(testNino, from, to)(
          status = SERVICE_UNAVAILABLE
        )

        val res: WSResponse = IncomeTaxViewChange.getPaymentDetails(testNino, from, to)

        res should have(
          httpStatus(INTERNAL_SERVER_ERROR)
        )
      }
    }
  }

}
