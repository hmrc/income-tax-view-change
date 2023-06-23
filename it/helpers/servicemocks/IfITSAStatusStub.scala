package helpers.servicemocks

import assets.ITSAStatusIntegrationTestConstants.{errorITSAStatusError, failedFutureITSAStatusError, taxYear, taxableEntityId}
import helpers.WiremockHelper
import play.mvc.Http.Status

object IfITSAStatusStub {

  def getITSAStatusUrl(taxableEntityId: String, taxYear: String, futureYears: Boolean = true, history: Boolean = true) = s"/income-tax/$taxableEntityId/person-itd/itsa-status/$taxYear?futureYears=$futureYears&history=$history"

  val url: String = getITSAStatusUrl(taxableEntityId, taxYear)

  def stubGetIfITSAStatusDetails(response: String): Unit = {
    WiremockHelper.stubGet(url, Status.OK, response)
  }

  def stubGetIfITSAStatusDetailsError(): Unit = {
    WiremockHelper.stubGet(url, Status.INTERNAL_SERVER_ERROR, failedFutureITSAStatusError.reason)
  }

  def stubGetIfITSAStatusDetailsBadRequest(): Unit = {
    WiremockHelper.stubGet(url, Status.BAD_REQUEST, errorITSAStatusError.reason)
  }

  def verifyGetIfITSAStatusDetails(): Unit = WiremockHelper.verifyGet(url)

}
