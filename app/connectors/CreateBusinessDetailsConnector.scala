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
import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.{CreateBusinessDetailsErrorResponse, IncomeSource}
import models.incomeSourceDetails.CreateIncomeSourceRequest
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

  def create(mtdbsaRef: String, createIncomeSourceRequest: CreateIncomeSourceRequest)(implicit headerCarrier: HeaderCarrier): Future[Either[CreateBusinessDetailsErrorResponse, List[IncomeSource]]] = {

    val url = s"${appConfig.desUrl}/income-tax/income-sources/mtdbsa/$mtdbsaRef/ITSA/business"

    logger.debug(s"[CreateBusinessDetailsConnector][create] - " +
      s"Calling POST $url \n\nHeaders: $headerCarrier \nAuth Headers: ${appConfig.desAuthHeaders}")

    http.POST[CreateIncomeSourceRequest, HttpResponse](url, createIncomeSourceRequest, appConfig.desAuthHeaders) map {
      case response if response.status == OK =>
        Logger("application").info(s"[CreateBusinessDetailsConnector][create] - SUCCESS - ${response.json}")
        response.json.validate[List[IncomeSource]].fold(
          invalidJson => {
            Logger("application").error(s"Invalid Json with $invalidJson")
            Left(CreateBusinessDetailsErrorResponse(response.status, response.body))
          },
          (res: List[IncomeSource]) => Right(res)
        )
      case errorResponse =>
        Logger("application").error(s"[CreateBusinessDetailsConnector][create] - Error with response code: ${errorResponse.status} and body: ${errorResponse.body}")
        Left(CreateBusinessDetailsErrorResponse(errorResponse.status, errorResponse.body))
    } recover {
      case ex =>
        logger.error(s"[CreateBusinessDetailsConnector][create] - ${ex.getMessage}")
        Left(CreateBusinessDetailsErrorResponse(Status.INTERNAL_SERVER_ERROR, s"${ex.getMessage}"))
    }
  }
}
