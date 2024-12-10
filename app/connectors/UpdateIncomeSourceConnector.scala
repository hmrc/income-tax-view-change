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
import models.updateIncomeSource.request.UpdateIncomeSourceRequestModel
import models.updateIncomeSource.{UpdateIncomeSourceResponse, UpdateIncomeSourceResponseError, UpdateIncomeSourceResponseModel}
import play.api.http.Status
import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UpdateIncomeSourceConnector @Inject()(val http: HttpClientV2,
                                            val appConfig: MicroserviceAppConfig)
                                           (implicit ec: ExecutionContext) extends RawResponseReads {

  val updateIncomeSourceUrl: String = s"${appConfig.ifUrl}/income-tax/business-detail/income-source"
  private val headers: Seq[(String, String)] = appConfig.getIFHeaders("1776")

  def updateIncomeSource(body: UpdateIncomeSourceRequestModel)(implicit headerCarrier: HeaderCarrier): Future[UpdateIncomeSourceResponse] = {
    val url = updateIncomeSourceUrl
    logger.info("INFO " +
      s"Calling PUT $url \n\nHeaders: $headerCarrier \nAuth Headers: $headers \nBody:$body")

    http
      .put(url"$url")
      .withBody(Json.toJson[UpdateIncomeSourceRequestModel](body))
      .setHeader(headers: _*)
      .execute[HttpResponse]
      .map {
        response =>
          response.status match {
            case OK =>
              logger.debug(s"RESPONSE status:${response.status}, body:${response.body}")
              response.json.validate[UpdateIncomeSourceResponseModel].fold(
                invalid => {
                  logger.error(s"Validation Errors: $invalid")
                  UpdateIncomeSourceResponseError(Status.INTERNAL_SERVER_ERROR, "Json Validation Error. Parsing IF Update Income Source")
                },
                valid => {
                  logger.info("successfully parsed response to UpdateIncomeSource")
                  valid
                }
              )
            case _ =>
              logger.error(s"RESPONSE status: ${response.status}, body: ${response.body}")
              UpdateIncomeSourceResponseError(response.status, response.body)
          }
      } recover {
      case ex =>
        logger.error(s"Unexpected failed future, ${ex.getMessage}")
        UpdateIncomeSourceResponseError(Status.INTERNAL_SERVER_ERROR, s"Unexpected failed future, ${ex.getMessage}")
    }
  }
}
