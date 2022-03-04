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

import play.api.libs.json.{JsPath, Json, OWrites, Reads, Writes}

package object models {

  def readNullable[T](path: JsPath)(implicit reads: Reads[T]): Reads[Option[T]] = path.readNullable[T] orElse Reads.pure[Option[T]](None)

  def readNullableSeq[T](path: JsPath)(implicit reads: Reads[Seq[T]]): Reads[Seq[T]] = path.read[Seq[T]] orElse Reads.pure[Seq[T]](Nil)

  def writeOptionList[T](fieldName: String)(implicit writes: Writes[T]): OWrites[Option[T]] = OWrites((ddlOpt: Option[T]) => {
    val json = ddlOpt match {
      case Some(ddl) => Json.toJson[T](ddl)
      case None => Json.arr()
    }
    Json.obj(fieldName -> json)
  })
}

