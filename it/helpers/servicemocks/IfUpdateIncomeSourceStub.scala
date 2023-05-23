package helpers.servicemocks

import assets.UpdateIncomeSourceIntegrationTestConstants.failureResponse
import helpers.WiremockHelper
import play.mvc.Http.Status

object IfUpdateIncomeSourceStub {


  val url: String ="/income-tax/business-detail/income-source"

  def stubPutIfUpdateCessationDate(request:String, response:String): Unit = {
    WiremockHelper.stubPut(url,Status.OK,request,response)
  }

  def stubPutIfUpdateCessationDateError(): Unit = {
    WiremockHelper.stubPut(url, Status.INTERNAL_SERVER_ERROR, failureResponse.reason )
  }

  def verifyPutIfUpdateCessationDate(requestBody:String): Unit = WiremockHelper.verifyPut(url,requestBody)

}
