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

package config

import javax.inject.Singleton
import javax.inject.Inject

import uk.gov.hmrc.http._
import uk.gov.hmrc.auth.core.PlayAuthConnector
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.config.{RunMode, ServicesConfig}
import uk.gov.hmrc.play.microservice.config.LoadAuditingConfig


@Singleton
class MicroserviceAuditConnector extends AuditConnector with RunMode {
  override lazy val auditingConfig = LoadAuditingConfig(s"auditing")
}

@Singleton
class MicroserviceAuthConnector @Inject()(val WSHttp: WSHttp) extends PlayAuthConnector with ServicesConfig {
  lazy val serviceUrl = baseUrl("auth")
  lazy val http = WSHttp
}

object MicroserviceAuditConnector extends MicroserviceAuditConnector

object MicroserviceAuthConnector extends MicroserviceAuthConnector(WSHttp)