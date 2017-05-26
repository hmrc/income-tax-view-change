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

package connectors

import javax.inject.{Inject, Singleton}

import models.{FinancialDataError, FinancialDataResult, FinancialDataSuccess}
import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{HeaderCarrier, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class FinancialDataConnector @Inject()(val http: HttpGet) extends ServicesConfig with RawResponseReads {

  lazy val desBaseUrl: String = baseUrl("des")
  val getFinancialDataUrl: String => String = mtditid => s"$desBaseUrl/calculation-store/financial-data/MTDBSA/$mtditid"

  def getFinancialData(mtditid: String)(implicit headerCarrier: HeaderCarrier): Future[FinancialDataResult] = {

    val url = getFinancialDataUrl(mtditid)
    Logger.debug(s"[FinancialDataConnector][getFinancialData] - GET $url")

    http.GET[HttpResponse](url) flatMap {
      response =>
        response.status match {
          case OK =>
            Logger.debug(s"[AuthConnector][getFinancialData] - RESPONSE status: ${response.status}, body: ${response.body}")
            Future.successful(FinancialDataSuccess(response.json))
          case _ =>
            Logger.warn(s"[FinancialDataConnector][getFinancialData] - RESPONSE status: ${response.status}, body: ${response.body}")
            Future.successful(FinancialDataError(response.status, response.body))
        }
    }
  }
}
