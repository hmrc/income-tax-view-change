package connectors

import java.time.LocalDate
import java.time.format.DateTimeFormatter

trait HipConnectors {

  // Headers constants
  val xMessageTypeFor1553 = "ETMPGetFinancialDetails"
  val xOriginatingSystem: String = "MDTP"
  val xTransmittingSystem : String = "HIP"

  def getMessageCreated: String = {
    val now = LocalDate.now
    val format = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ssZ")
    format.format(now)
  }

  // Query string param constants
  val calculateAccruedInterest: String = "true"
  val customerPaymentInformation: String = "true"
  val removePaymentonAccount: String = "false"
  val idType: String = "NINO"
  val regimeType: String = "ITSA"

  val includeLocks: String = "true"
  val includeStatistical: String = "false"
}
