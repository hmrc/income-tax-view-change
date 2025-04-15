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

package config

import models.hip.HipApi
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.util.{Base64, UUID}
import javax.inject.{Inject, Singleton}

@Singleton
class MicroserviceAppConfig @Inject()(servicesConfig: ServicesConfig) {

  private def loadConfig(key: String) = servicesConfig.getString(key)

  val desUrl: String = loadConfig("microservice.services.des.url")
  val desEnvironment: String = loadConfig("microservice.services.des.environment")
  val desToken: String = s"Bearer ${loadConfig("microservice.services.des.authorization-token")}"

  val desAuthHeaders: Seq[(String, String)] = {
    Seq(
      "Environment" -> desEnvironment,
      "Authorization" -> desToken
    )
  }

  val ifUrl: String = loadConfig("microservice.services.if.url")
  val ifAuthHeaders: Seq[(String, String)] = {
    val ifEnvironment: String = loadConfig("microservice.services.if.environment")
    val ifToken: String = loadConfig("microservice.services.if.authorization-token")
    Seq(
      "Gov-Test-Scenario" -> "businessDetailsIf",
      "Environment" -> ifEnvironment,
      "Authorization" -> s"Bearer $ifToken"
    )
  }

  def getIFHeaders(api: String): Seq[(String, String)] = {
    val ifEnvironment: String = loadConfig("microservice.services.if.environment")
    val ifToken: String = loadConfig(s"microservice.services.if.authorization-token-$api")
    Seq(
      "Environment" -> ifEnvironment,
      "Authorization" -> s"Bearer $ifToken"
    )
  }

  lazy val hipUrl: String = servicesConfig.baseUrl("hip")

  private def getHipCredentials(hipApi: HipApi): String = {
    val clientId = loadConfig(s"microservice.services.hip.${hipApi.name}.clientId")
    val secret = loadConfig(s"microservice.services.hip.${hipApi.name}.secret")

    val encoded = Base64.getEncoder.encodeToString(s"$clientId:$secret".getBytes("UTF-8"))

    s"Basic $encoded"
  }

  def getHIPHeaders(hipApi: HipApi): Seq[(String, String)] = {
    Seq(
      (HeaderNames.authorisation, getHipCredentials(hipApi)),
      ("correlationId", UUID.randomUUID().toString)
    )
  }

  def hipFeatureSwitchEnabled(hipApi: HipApi): Boolean = {
    servicesConfig.getBoolean(s"microservice.services.hip.${hipApi.name}.feature-switch")
  }

  val claimToAdjustTimeout: Int = servicesConfig.getInt("claim-to-adjust.timeout")

  val confidenceLevel: Int = servicesConfig.getInt("auth.confidenceLevel")

  val useRepaymentHistoryDetailsIFPlatform: Boolean = servicesConfig.getBoolean("useRepaymentHistoryDetailsIFPlatform")
  val useGetCalcListIFPlatform: Boolean = servicesConfig.getBoolean("useGetCalcListIFPlatform")
}
