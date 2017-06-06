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

package models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.scalatest.Matchers
import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class ObligationsModelSpec extends UnitSpec with Matchers{

  val localDate: String => LocalDate = date => LocalDate.parse(date, DateTimeFormatter.ofPattern("uuuu-M-d"))

  "The ObligationsModel" should {

    val jsonString =
      """
        {
          "obligations": [
            {
              "start": "2017-04-01",
              "end": "2017-06-30",
              "due": "2017-07-31",
              "met": true
            },
            {
              "start": "2017-07-01",
              "end": "2017-09-30",
              "due": "2017-10-31",
              "met": false
            }
          ]
        }
        """.stripMargin.split("\\s+").mkString

    val obligation1 = ObligationModel(
      start = localDate("2017-04-01"),
      end = localDate("2017-6-30"),
      due = localDate("2017-7-31"),
      met = true
    )
    val obligation2 = ObligationModel(
      start = localDate("2017-7-1"),
      end = localDate("2017-9-30"),
      due = localDate("2017-10-31"),
      met = false
    )

    val obligations = ObligationsModel(Seq(obligation1, obligation2))

    "for the 1st Obligation" should {

      "have the start date as 1st April 2017" in {
        obligations.obligations.head.start shouldBe localDate("2017-4-1")
      }

      "have the end date as 30th June 2017" in {
        obligations.obligations.head.end shouldBe localDate("2017-6-30")
      }

      "have the due date as 31st July 2017" in {
        obligations.obligations.head.due shouldBe localDate("2017-7-31")
      }

      "have the obligation met status as 'true'" in {
        obligations.obligations.head.met shouldBe true
      }
    }

    "for the 2nd Obligation" should {

      "have the start date as 1st July 2017" in {
        obligations.obligations.last.start shouldBe localDate("2017-7-1")
      }

      "have the end date as 30th September 2017" in {
        obligations.obligations.last.end shouldBe localDate("2017-9-30")
      }

      "have the due date as 31st October 2017" in {
        obligations.obligations.last.due shouldBe localDate("2017-10-31")
      }

      "have the obligation met status as 'false'" in {
        obligations.obligations.last.met shouldBe false
      }
    }

    "be formatted to JSON correctly" in {
      Json.toJson(obligations).toString() shouldBe jsonString
    }

    "be able to parse a JSON to string into the Model" in {
      Json.parse(jsonString).as[ObligationsModel] shouldBe obligations
    }
  }
}
