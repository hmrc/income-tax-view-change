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

package models.obligations

import constants.ObligationsTestConstants._
import models.obligations.ObligationStatus.{Fulfilled, Open}
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import utils.TestSupport
import models.obligations.SingleObligationModel._

class SingleObligationModelSpec extends TestSupport with Matchers {

  "The ReportDeadlineModel" when {

    "a Deadline is Received" should {

      "read from the json with an incomeSourceType of ITSA" in {
        Json.fromJson(testReceivedDeadlineFromJson())(SingleObligationModel.desReadsApi("ITSA")) shouldBe JsSuccess(testReceivedDeadlineCrystallised())
      }

      "read from the json with an incomeSourceType that is not ITSA and periodKey is EOPS" in {
        Json.fromJson(testReceivedEOPSDeadlineFromJson())(SingleObligationModel.desReadsApi("ITSB")) shouldBe JsSuccess(testReceivedDeadlineEOPS)
      }

      "read from the json with an incomeSourceType that is not ITSA and periodKey is not EOPS" in {
        Json.fromJson(testReceivedDeadlineFromJson())(SingleObligationModel.desReadsApi("ITSB")) shouldBe JsSuccess(testReceivedDeadlineQuarterly())
      }

      "write to Json" in {
        Json.toJson(testReceivedDeadlineQuarterly()) shouldBe testReceivedDeadlineToJson()
      }
    }

    "a Deadline is Not Received" should {

      "read from the DES Json" in {
        Json.fromJson(testDeadlineFromJson())(SingleObligationModel.desReadsApi("ITSB")) shouldBe JsSuccess(testDeadline())
      }

      "write to Json" in {
        Json.toJson(testDeadline()) shouldBe testDeadlineToJson()
      }
    }

    "a Deadline is Received with Status" should {

      s"where status is ${Open.code}" in {
        Json.fromJson(testReceivedDeadlineFromJson(status =  Open.code))(SingleObligationModel.desReadsApi("ITSA")) shouldBe JsSuccess(testReceivedDeadlineCrystallised(Open.name))
      }

      s"where status is ${Fulfilled.code}" in {
        Json.fromJson(testReceivedDeadlineFromJson(status =  Fulfilled.code))(SingleObligationModel.desReadsApi("ITSA")) shouldBe JsSuccess(testReceivedDeadlineCrystallised(Fulfilled.name))
      }

      s"where status is X" in {
        Json.fromJson(testReceivedDeadlineFromJson(status =  "X"))(SingleObligationModel.desReadsApi("ITSA")) shouldBe JsSuccess(testReceivedDeadlineCrystallised(status = "X"))
      }
    }

  }

}
