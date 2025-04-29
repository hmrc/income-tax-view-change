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

package models.hip.core

import play.api.libs.json.{Format, Json}


sealed trait NinoResponse

case class NinoModel(nino: String) extends NinoResponse

case class NinoErrorModel(status: Int, reason: String) extends NinoResponse

object NinoModel {
  implicit val format: Format[NinoModel] = Json.format[NinoModel]
}

object NinoErrorModel {
  implicit val format: Format[NinoErrorModel] = Json.format[NinoErrorModel]
}