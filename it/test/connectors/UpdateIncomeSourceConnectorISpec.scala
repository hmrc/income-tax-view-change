/*
 * Copyright 2024 HM Revenue & Customs
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

import helpers.ComponentSpecBase

class UpdateIncomeSourceConnectorISpec extends ComponentSpecBase{

  val connector: UpdateIncomeSourceConnector = app.injector.instanceOf[UpdateIncomeSourceConnector]

  val url: String = "/income-tax/business-detail/income-source"


  "UpdateIncomeSourceConnector" when {
    "updateIncomeSource is called" when {
      "the response is an Ok - 200" should {
        "return a UpdateIncomeSourceResponseModel when successful" in {

        }
        "return a UpdateIncomeSourceRequestError when unable to parse the data into a UpdateIncomeSourceResponseModel" in {

        }
      }
      "the response is not 200" should {
        "return a UpdateIncomeSourceResponseError" when {
          "the response is a 404 - Not Found" in {

          }
          "the response is a 500 - InternalServerError" in {

          }
        }
      }

    }
  }

}
