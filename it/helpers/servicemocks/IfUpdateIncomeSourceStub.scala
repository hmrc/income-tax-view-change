package helpers.servicemocks

import assets.UpdateIncomeSourceIntegrationTestConstants.failureResponse
import helpers.WiremockHelper
import play.api.libs.json.Json
import play.mvc.Http.Status

object IfUpdateIncomeSourceStub {


  val url: String = "/income-tax/business-detail/income-source"

  def stubPutIfUpdateIncomeSource(request: String, response: String): Unit = {
    WiremockHelper.stubPut(url, Status.OK, request, response)
  }

  def stubPutIfUpdateIncomeSourceError(): Unit = {
    WiremockHelper.stubPut(url, Status.INTERNAL_SERVER_ERROR, Json.toJson(failureResponse).toString())
  }

  def verifyPutIfUpdateIncomeSource(requestBody: String): Unit = WiremockHelper.verifyPut(url, requestBody)

}
