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

package connectors.hip

import config.MicroserviceAppConfig
import models.hip.CreateIncomeSourceHipApi
import models.hip.createIncomeSource.CreateIncomeSourceHipRequest
import models.hip.incomeSourceDetails.{CreateBusinessDetailsHipErrorResponse, CreateBusinessDetailsHipModel, IncomeSource}
import play.api.Logger
import play.api.http.Status
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateBusinessDetailsHipConnector @Inject()(val http: HttpClientV2,
                                                  val appConfig: MicroserviceAppConfig)
                                                 (implicit ec: ExecutionContext) extends HipConnectorDataHelper {

  val getUrl: String = s"${appConfig.hipUrl}/etmp/RESTAdapter/itsa/taxpayer/income-source"

  def getHeaders: Seq[(String, String)] = appConfig.getHIPHeaders(CreateIncomeSourceHipApi, Some(xMessageTypeFor5265))

  def create(body: CreateIncomeSourceHipRequest)
            (implicit headerCarrier: HeaderCarrier): Future[Either[CreateBusinessDetailsHipErrorResponse, List[IncomeSource]]] = {

    val hc: HeaderCarrier = headerCarrier
      .copy(authorization = Some(Authorization(appConfig.desToken)))
      .withExtraHeaders("Environment" -> appConfig.desEnvironment)

    logWithDebug(s"Calling POST $getUrl \n\nHeaders: $headerCarrier \nAuth Headers: $getHeaders")

    http.post(url"$getUrl")(hc)
      .setHeader(getHeaders: _*)
      .withBody(Json.toJson[CreateIncomeSourceHipRequest](body))
      .execute[HttpResponse]
      .map {
      case response if response.status == CREATED =>
        logWithInfo(s"SUCCESS - ${response.json}")
        response.json.validate[CreateBusinessDetailsHipModel].fold(
          invalidJson => {
            logWithError(s"Invalid Json with $invalidJson")
            Left(CreateBusinessDetailsHipErrorResponse(response.status, response.body))
          },
          (res: CreateBusinessDetailsHipModel) => Right(res.success.incomeSourceIdDetails)
        )
      case errorResponse =>
        logWithError(s"Error with response code: ${errorResponse.status} and body: ${errorResponse.json}")
        Left(CreateBusinessDetailsHipErrorResponse(errorResponse.status, errorResponse.json.toString()))
    } recover {
      case ex =>
        logWithError(s"[CreateBusinessDetailsHipConnector] Unexpected error: ${ex.getMessage}")
        Left(CreateBusinessDetailsHipErrorResponse(Status.INTERNAL_SERVER_ERROR, s"${ex.getMessage}"))
    }
  }

  private val logWithError: String => Unit = message => Logger("application").error(message)
  private val logWithDebug: String => Unit = message => Logger("application").debug(message)
  private val logWithInfo:  String => Unit = message => Logger("application").info(message)
}
