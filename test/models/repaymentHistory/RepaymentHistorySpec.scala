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

package models.repaymentHistory

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsValue, Json}

class RepaymentHistorySpec extends WordSpec with Matchers {


  val repaymentHistoryFull: RepaymentHistory = RepaymentHistory(
    amountApprovedforRepayment = Some(100.0),
    amountRequested = 200.0,
    repaymentMethod = "BACD",
    totalRepaymentAmount = 300.0,
    repaymentItems = Seq[RepaymentItem](
      RepaymentItem(
        Seq(
          RepaymentSupplementItem(
            parentCreditReference = Some("002420002231"),
            amount = Some(400.0),
            fromDate = Some("2021-07-23"),
            toDate = Some("2021-08-23"),
            rate = Some(500.0)
          )
        )
      )
    ),
    estimatedRepaymentDate = "2021-01-21",
    creationDate = "2020-12-28",
    repaymentRequestNumber = "000000003135"
  )

  val repaymentHistoryFullJson: JsValue = Json.obj(
    "repaymentRequestNumber" -> "000000003135",
    "amountApprovedforRepayment" -> Some(100.0),
    "amountRequested" -> 200.0,
    "repaymentMethod" -> "BACD",
    "totalRepaymentAmount" -> 300.0,
    "repaymentItems" -> Json.arr(
      Json.obj(
        "repaymentSupplementItem" -> Json.arr(
          Json.obj(
            "parentCreditReference" -> Some("002420002231"),
            "amount" -> Some(400.0),
            "fromDate" -> Some("2021-07-23"),
            "toDate" -> Some("2021-08-23"),
            "rate" -> Some(500.0)
          )
        )
      )
    ),
    "estimatedRepaymentDate" -> "2021-01-21",
    "creationDate" -> "2020-12-28"
  )

  "RepaymentHistory" should {
    "write to Json" when {
      "the model has all details" in {
        Json.toJson(repaymentHistoryFull) shouldBe repaymentHistoryFullJson
      }
    }
  }
}
