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

package helpers.servicemocks

import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import helpers.WiremockHelper
import uk.gov.hmrc.auth.core.ConfidenceLevel

object AuthStub {

  val postAuthoriseUrl = "/auth/authorise"

  private def successfulAuthResponse(confidenceLevel: Option[ConfidenceLevel]): JsObject = {
    confidenceLevel.fold(Json.obj())(unwrappedConfidenceLevel => Json.obj("confidenceLevel" -> unwrappedConfidenceLevel))
  }

  def stubAuthorised(): Unit = {
    WiremockHelper.stubPost(postAuthoriseUrl, Status.OK, successfulAuthResponse(Some(ConfidenceLevel.L250)).toString())
  }

  def stubUnauthorised(): Unit = {
    WiremockHelper.stubPost(postAuthoriseUrl, Status.UNAUTHORIZED, "{}")
  }
}
