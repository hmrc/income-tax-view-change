/*
 * Copyright 2022 HM Revenue & Customs
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

import connectors.{FinancialDetailsConnector, FinancialDetailsConnectorDES, FinancialDetailsConnectorIF}
import org.scalatest.{Matchers, WordSpecLike}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.reflect.ClassTag

class ITVCModuleSpec extends WordSpecLike with Matchers {


  "ITVCModule" should {

    "bind DES FinancialDetailsConnector" when {
      "config flag is false" in new Setup("microservice.services.if.enabled" -> false) {
        instanceOf[FinancialDetailsConnector] shouldBe a[FinancialDetailsConnectorDES]
      }
    }

    "bind IF FinancialDetailsConnector" when {
      "config flag is true" in new Setup("microservice.services.if.enabled" -> true) {
        instanceOf[FinancialDetailsConnector] shouldBe a[FinancialDetailsConnectorIF]
      }
    }
}

  private class Setup(conf: (String, Any)*) {
    val application: Application = new GuiceApplicationBuilder()
      .configure(conf: _*)
      .build()

    def instanceOf[T: ClassTag]: T = application.injector.instanceOf[T]
  }

}
