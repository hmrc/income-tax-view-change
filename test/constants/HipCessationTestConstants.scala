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

import models.hip.core.CessationModel
import play.api.libs.json.Json

import java.time.LocalDate

object HipCessationTestConstants {

  val testCessationModel = CessationModel(
    Some(LocalDate.parse("2017-06-01"))
  )

  val testCessationJson = Json.obj(
    "cessationDate" -> "2017-06-01"
  )

  val testCessationToJson = Json.obj(
    "date" -> "2017-06-01"
  )
}
