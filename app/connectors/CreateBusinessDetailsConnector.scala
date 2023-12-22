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
import models.createIncomeSource.CreateIncomeSourceRequest
import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.{CreateBusinessDetailsErrorResponse, IncomeSource}
import play.api.Logger
import play.api.http.Status
import play.api.http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateBusinessDetailsConnector @Inject()(val http: HttpClient,
                                               val appConfig: MicroserviceAppConfig)
                                              (implicit ec: ExecutionContext) extends RawResponseReads {

  val url: String => String = mtdbsaRef => s"${appConfig.desUrl}/income-tax/income-sources/mtdbsa/$mtdbsaRef/ITSA/business"

  def create(mtdbsaRef: String, body: CreateIncomeSourceRequest)
            (implicit headerCarrier: HeaderCarrier): Future[Either[CreateBusinessDetailsErrorResponse, List[IncomeSource]]] = {

    logger.debug(withPrefix(s"Calling POST ${url(mtdbsaRef)} \n\nHeaders: $headerCarrier \nAuth Headers: ${appConfig.desAuthHeaders}"))

    http.POST[CreateIncomeSourceRequest, HttpResponse](url(mtdbsaRef), body, appConfig.desAuthHeaders) map {
      case response if response.status == OK =>
        Logger("application").info(withPrefix(s"SUCCESS - ${response.json}"))
        response.json.validate[List[IncomeSource]].fold(
          invalidJson => {
            Logger("application").error(withPrefix(s"Invalid Json with $invalidJson"))
            Left(CreateBusinessDetailsErrorResponse(response.status, response.body))
          },
          (res: List[IncomeSource]) => Right(res)
        )
      case errorResponse =>
        Logger("application").error(withPrefix(s"Error with response code: ${errorResponse.status} and body: ${errorResponse.json}"))
        Left(CreateBusinessDetailsErrorResponse(errorResponse.status, errorResponse.json.toString()))
    } recover {
      case ex =>
        logger.error(withPrefix(s"${ex.getMessage}"))
        Left(CreateBusinessDetailsErrorResponse(Status.INTERNAL_SERVER_ERROR, s"${ex.getMessage}"))
    }
  }

  private val withPrefix: String => String = suffix => "[CreateBusinessDetailsConnector][create] - " + suffix
}
