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

package constants

import models.hip.core.ContactDetailsModel
import play.api.libs.json.Json

object HipContactDetailsTestConstants {

  val testContactDetailsModel = ContactDetailsModel(
    phoneNumber = Some("01332752856"),
    mobileNumber = Some("07782565326"),
    faxNumber = Some("01332754256"),
    emailAddress = Some("stephen@manncorpone.co.uk"))

  val testContactDetailsToJsonWrites = Json.obj(
    "phoneNumber" -> "01332752856",
    "mobileNumber" -> "07782565326",
    "faxNumber" -> "01332754256",
    "emailAddress" -> "stephen@manncorpone.co.uk"
  )

  val hipTestContactDetailsJson = Json.obj(
    "phoneNumber" -> "01332752856",
    "mobileNumber" -> "07782565326",
    "faxNumber" -> "01332754256",
    "emailAddress" -> "stephen@manncorpone.co.uk"
  )

  val testSomeContactDetailsModel = ContactDetailsModel(None, None, None, emailAddress = Some("stephen@manncorpone.co.uk"))

  val testContactDetailsJson = Json.obj(
    "telephone" -> "01332752856",
    "mobileNo" -> "07782565326",
    "faxNo" -> "01332754256",
    "email" -> "stephen@manncorpone.co.uk"
  )

  val testSomeContactDetailsJson = Json.obj(
    "email" -> "stephen@manncorpone.co.uk"
  )

  val testContactDetailsToJson = Json.obj(
    "emailAddress" -> "stephen@manncorpone.co.uk"
  )

}
