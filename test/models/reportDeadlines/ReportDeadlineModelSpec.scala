/*
 * Copyright 2019 HM Revenue & Customs
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

package models.reportDeadlines

import assets.ReportDeadlinesTestConstants._
import org.scalatest.Matchers
import play.api.libs.json._
import utils.TestSupport

class ReportDeadlineModelSpec extends TestSupport with Matchers {

  "The ReportDeadlineModel" when {

    "a Deadline is Received" should {

      "read from the json with an incomeSourceType of ITSA" in {
        Json.fromJson(testReceivedDeadlineFromJson)(ReportDeadlineModel.desReadsApi("ITSA")) shouldBe JsSuccess(testReceivedDeadlineCrystallised)
      }

      "read from the json with an incomeSourceType that is not ITSA and periodKey is EOPS" in {
        Json.fromJson(testReceivedEOPSDeadlineFromJson)(ReportDeadlineModel.desReadsApi("ITSB")) shouldBe JsSuccess(testReceivedDeadlineEOPS)
      }

      "read from the json with an incomeSourceType that is not ITSA and periodKey is not EOPS" in {
        Json.fromJson(testReceivedDeadlineFromJson)(ReportDeadlineModel.desReadsApi("ITSB")) shouldBe JsSuccess(testReceivedDeadlineQuarterly)
      }

      "write to Json" in {
        Json.toJson(testReceivedDeadlineQuarterly) shouldBe testReceivedDeadlineToJson
      }
    }

    "a Deadline is Not Received" should {

      "read from the DES Json" in {
        Json.fromJson(testDeadlineFromJson)(ReportDeadlineModel.desReadsApi("ITSB")) shouldBe JsSuccess(testDeadline)
      }

      "write to Json" in {
        Json.toJson(testDeadline) shouldBe testDeadlineToJson
      }
    }

  }

}
