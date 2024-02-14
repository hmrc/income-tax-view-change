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

import utils.TestSupport

class MicroserviceAppConfigSpec extends TestSupport {

  "MicroserviceAppConfig" should {

    "read from application.conf the values for calling DES APIs" which {
      "has a correct DES base url" in {
        microserviceAppConfig.desUrl shouldBe "http://localhost:9084"
      }

      "has a correct HTTP headers for DES" in {
        microserviceAppConfig.desAuthHeaders shouldBe Seq(
          "Environment" -> "localDESEnvironment",
          "Authorization" -> "Bearer localDESToken"
        )
      }
    }

    "read from application.conf the values for calling IF APIs" which {

      "has a correct IF base url" in {
        microserviceAppConfig.ifUrl shouldBe "http://localhost:9084"
      }

      "has a correct HTTP headers for IF" in {
        microserviceAppConfig.ifAuthHeaders shouldBe Seq(
          "Gov-Test-Scenario" -> "businessDetailsIf",
          "Environment" -> "localIFEnvironment",
          "Authorization" -> "Bearer localIFToken"
        )
      }
    }

  }

}
