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

package models.itsaStatus

import constants.ITSAStatusTestConstants._
import org.scalatest.matchers.should.Matchers
import play.api.http.Status
import play.api.libs.json.{JsSuccess, Json}
import utils.TestSupport

class ITSAStatusResponseSpec extends TestSupport with Matchers {
  "The ITSAStatusResponseModel" when {
    "the response is from IF" should {
      "read response to model " in {
        Json.fromJson(successITSAStatusResponseJson)(ITSAStatusResponseModel.format) shouldBe JsSuccess(successITSAStatusResponseModel)
      }

      "read minimal response to model" in {
        Json.fromJson(successITSAStatusResponseModelMinimalJson)(ITSAStatusResponseModel.format) shouldBe JsSuccess(successITSAStatusResponseModelMinimal)
      }

      "read StatusDetailMinimal response to model" in {
        Json.fromJson(statusDetailMinimalJson)(StatusDetail.readsStatusDetail) shouldBe JsSuccess(statusDetailMinimal)
      }
    }

    "the response is from HIP" that {
      "has no statusDetails" should {
        "read to minimal response to model" in {
          Json.fromJson(successITSAStatusResponseModelMinimalJson)(ITSAStatusResponseModel.format) shouldBe JsSuccess(successITSAStatusResponseModelMinimal)
        }
      }
      StatusDetail.statusMapping.foreach { case (statusKey, status) =>
        StatusDetail.statusReasonMapping.foreach { case (statusReasonKey, statusReason) =>
          s"has a status of $statusKey and statusReason of $statusReasonKey" should {
            s"read and convert status to $status and statusReason to $statusReason" when {
              "all success response" in {
                Json.fromJson(successITSAStatusResponseHipJson(statusKey, statusReasonKey))(ITSAStatusResponseModel.format) shouldBe JsSuccess(successITSAStatusResponseModelHip(status, statusReason))
              }
              "StatusDetailMinimal response" in {
                Json.fromJson(statusDetailMinHipJson(statusKey, statusReasonKey))(StatusDetail.readsStatusDetail) shouldBe JsSuccess(statusDetailMinimalHip(status, statusReason))
              }
            }
          }
        }
      }
    }
  }

  "The ITSAStatusResponseError" should {
    "have the correct status code in the model" in {
      errorITSAStatusError.status shouldBe Status.BAD_REQUEST
    }
    "have the correct Error Message" in {
      errorITSAStatusError.reason shouldBe "Dummy message"
    }
  }

  "The ITSAStatusResponseNotFoundError" should {
    "have the correct status code in the model" in {
      errorITSAStatusNotFoundError.status shouldBe Status.NOT_FOUND
    }
    "have the correct Error Message" in {
      errorITSAStatusNotFoundError.reason shouldBe "Dummy message"
    }
  }
}
