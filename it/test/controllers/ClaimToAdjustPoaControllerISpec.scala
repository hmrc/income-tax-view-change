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

package controllers

import models.claimToAdjustPoa.{ClaimToAdjustPoaRequest, MainIncomeLower}
import play.api.http.Status._
import play.api.libs.json.Json
import test.helpers.{ComponentSpecBase, WiremockHelper}

class ClaimToAdjustPoaControllerISpec extends ComponentSpecBase {

  val apiUrl = "/income-tax/calculations/POA/ClaimToAdjust"

  "Calling /submit-claim-to-adjust-poa" when {

    val request = ClaimToAdjustPoaRequest(
      "AA000000A",
      "2024",
      1000.0,
      MainIncomeLower
    )

    "unauthorised" should {

      s"return a $UNAUTHORIZED response" in {

        isAuthorised(false)

        When(s"I call POST /submit-claim-to-adjust-poa")

        val res = IncomeTaxViewChange.postClaimToAdjustPoa(
          Json.toJson(request)
        )

        res should have(httpStatus(UNAUTHORIZED))
      }
    }

    "authorised with a valid request" when {

      "A successful response is received from the API" should {

        s"return a successful $CREATED response" in {

          isAuthorised(true)

          val response = Json.obj(
            "successResponse" -> Json.obj(
              "processingDate" -> "2024-01-31T09:27:17Z"
            )
          )

          WiremockHelper.stubPost(
            url = apiUrl,
            status = CREATED,
            requestBody = Json.stringify(Json.toJson(request)),
            responseBody = Json.stringify(Json.toJson(response)))

          When(s"I call POST /submit-claim-to-adjust-poa")

          val res = IncomeTaxViewChange.postClaimToAdjustPoa(
            Json.toJson(request)
          )

          res should have(httpStatus(CREATED))
          val json = Json.parse(res.body)
          json shouldBe Json.obj("processingDate" -> "2024-01-31T09:27:17Z")
        }
      }

      "A failure response is received from the API" should {

        s"return $BAD_REQUEST and code from API" in {

          isAuthorised(true)

          WiremockHelper.stubPost(
            url = apiUrl,
            status = BAD_REQUEST,
            requestBody = Json.stringify(Json.toJson(request)),
            responseBody = Json.stringify(Json.obj(
              "failures" -> Json.arr(
                Json.obj(
                  "code" -> "INVALID_PAYLOAD",
                  "reason" -> "Submission has not passed validation. Invalid payload.")))))

          When(s"I call POST /submit-claim-to-adjust-poa")
          val res = IncomeTaxViewChange.postClaimToAdjustPoa(
            Json.toJson(request)
          )

          Then(s"a status of $BAD_REQUEST is returned ")

          res should have(httpStatus(BAD_REQUEST))
          val json = Json.parse(res.body)
          json shouldBe Json.obj(
            "message" -> "INVALID_PAYLOAD"
          )
        }
      }
    }

    "authorised with a invalid request" should {

      s"return $BAD_REQUEST" in {

        isAuthorised(true)

        WiremockHelper.stubPost(
          url = apiUrl,
          status = BAD_REQUEST,
          requestBody = Json.stringify(Json.obj()),
          responseBody = Json.stringify(Json.obj(
            "code" -> "INVALID_PAYLOAD",
            "reason" -> "Submission has not passed validation. Invalid payload.")))

        val invalidRequest = Json.obj()

        When(s"I call POST /submit-claim-to-adjust-poa")
        val res = IncomeTaxViewChange.postClaimToAdjustPoa(
          Json.toJson(invalidRequest)
        )

        Then(s"a status of $BAD_REQUEST is returned ")

        res should have(httpStatus(BAD_REQUEST))
        val json = Json.parse(res.body)
        json shouldBe Json.obj(
          "message" -> "Could not validate request"
        )
      }
    }
  }
}
