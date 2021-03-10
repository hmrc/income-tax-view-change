
package helpers.servicemocks

import helpers.WiremockHelper
import play.api.libs.json.{JsValue, Json}

object DesOutStandingChargesStub {
  private def url(idType: String, idNumber: Long, taxYearEndDate: String): String = {
    s"/income-tax/charges/outstanding/$idType/$idNumber/$taxYearEndDate"
  }

  def stubGetOutStandingChargeDetails(idType: String, idNumber: Long, taxYearEndDate: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    WiremockHelper.stubGet(url(idType, idNumber, taxYearEndDate), status, response.toString)
  }

}
