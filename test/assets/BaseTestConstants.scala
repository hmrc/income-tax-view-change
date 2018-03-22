/*
 * Copyright 2018 HM Revenue & Customs
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

package assets

import play.api.libs.json.Json
import play.mvc.Http.Status
import uk.gov.hmrc.http.HttpResponse
import assets.IncomeSourceDetailsTestConstants.testIncomeSourceDetailsJson

object BaseTestConstants {

  val testNino = "BB123456A"
  val mtdRef = "123456789012345"

  //Connector Responses
  val successResponse = HttpResponse(Status.OK, Some(testIncomeSourceDetailsJson))
  val badJson = HttpResponse(Status.OK, Some(Json.toJson("{}")))
  val badResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, responseString = Some("Dummy error message"))
}
