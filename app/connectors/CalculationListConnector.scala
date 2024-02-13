/*
 * Copyright 2023 HM Revenue & Customs
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

package connectors

import config.MicroserviceAppConfig
import connectors.httpParsers.CalculationListHttpParser.CalculationListReads
import connectors.httpParsers.CalculationListHttpParser.HttpGetResult
import models.calculationList.CalculationListResponseModel
import play.api.Logging
import play.api.http.Status.OK
import play.api.mvc.Result
import play.api.mvc.Results.{InternalServerError, Ok}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CalculationListConnector @Inject()(val http: HttpClient, val appConfig: MicroserviceAppConfig) extends Logging {

  private[connectors] def getCalculationListUrl(nino: String, taxYearEnd: String): String =
    s"${appConfig.desUrl}/income-tax/list-of-calculation-results/$nino?taxYear=$taxYearEnd"

  private[connectors] def getCalculationListTYSUrl(nino: String, taxYearRange: String): String =
    s"${appConfig.ifUrl}/income-tax/view/calculations/liability/$taxYearRange/$nino"

  private[connectors] def getOverwriteCalculationListTYSUrl(nino: String, taxYearRange: String, crystallisationStatus: String): String =
    s"${appConfig.ifUrl}/income-tax/view/calculations/liability/$taxYearRange/$nino/overwrite/$crystallisationStatus"

  def getCalculationList(nino: String, taxYear: String)
                        (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CalculationListResponseModel]] = {
    val url = getCalculationListUrl(nino, taxYear)

    logger.debug(s"[CalculationListConnector][getCalculationList] - Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: ${appConfig.desAuthHeaders}")
    http.GET(url = url, headers = appConfig.desAuthHeaders)(CalculationListReads, headerCarrier, ec)
  }

  def getCalculationListTYS(nino: String, taxYear: String)
                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[CalculationListResponseModel]] = {
    val url = getCalculationListTYSUrl(nino, taxYear)

    logger.debug(s"[CalculationListConnector][getCalculationListTYS] - Calling GET $url \nHeaders: $headerCarrier \nAuth Headers: ${appConfig.getIFHeaders("1896")}")
    http.GET(url = url, headers = appConfig.getIFHeaders("1896"))(CalculationListReads, headerCarrier, ec)

  }

  implicit val httpReads: HttpReads[HttpResponse] = (method: String, url: String, response: HttpResponse) => response

  def getOverwriteCalculationListTYS(nino: String, taxYear: String, crystallisationStatus: String)
                           (implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Result] = {
    val url = getOverwriteCalculationListTYSUrl(nino, taxYear, crystallisationStatus)

    println("CCCCCCCCC" + url)
    http.GET(url = url, headers = appConfig.getIFHeaders("1896"))(httpReads, headerCarrier, ec).flatMap{
      response => response.status match {
        case OK => Future.successful(Ok("Success"))
        case _ => Future.successful(InternalServerError("Failure"))
      }
    }

  }

}
