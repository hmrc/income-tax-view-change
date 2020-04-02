/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

trait DesConnector {

  val http: HttpClient
  val appConfig: MicroserviceAppConfig

  val desUrl: String = appConfig.desUrl

  def desHC(headerCarrier: HeaderCarrier): HeaderCarrier = headerCarrier.copy(authorization = Some(Authorization(s"Bearer ${appConfig.desToken}")))
    .withExtraHeaders("Environment" -> appConfig.desEnvironment)

  def desGet[A](url: String)(implicit rds: HttpReads[A], headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[A] = {
    http.GET(url)(rds, desHC(headerCarrier), implicitly)
  }
}
