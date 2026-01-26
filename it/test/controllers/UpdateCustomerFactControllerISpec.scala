/*
 * Copyright 2026 HM Revenue & Customs
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

import helpers.ComponentSpecBase
import helpers.servicemocks.HipUpdateCustomerFactStub
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, UNAUTHORIZED, UNPROCESSABLE_ENTITY}

class UpdateCustomerFactControllerISpec extends ComponentSpecBase {

  private val mtdId = "testMtdId"

  "Calling PUT /customer-facts/update/:mtdId" when {

    "authorised" when {

      "HIP returns 200" should {
        "return 200" in {
          isAuthorised(true)

          HipUpdateCustomerFactStub.stubPutUpdateCustomerFactSuccess(mtdId)

          When(s"I call PUT /customer-facts/update/$mtdId")
          val res = IncomeTaxViewChange.putUpdateCustomerFacts(mtdId)

          HipUpdateCustomerFactStub.verifyPutUpdateCustomerFact(mtdId)

          res should have(
            httpStatus(OK)
          )
        }
      }

      "HIP returns 400" should {
        "return 400" in {
          isAuthorised(true)

          HipUpdateCustomerFactStub.stubPutUpdateCustomerFactBadRequest(mtdId)

          When(s"I call PUT /customer-facts/update/$mtdId")
          val res = IncomeTaxViewChange.putUpdateCustomerFacts(mtdId)

          HipUpdateCustomerFactStub.verifyPutUpdateCustomerFact(mtdId)

          res should have(
            httpStatus(BAD_REQUEST)
          )
        }
      }

      "HIP returns 422" should {
        "return 422" in {
          isAuthorised(true)

          HipUpdateCustomerFactStub.stubPutUpdateCustomerFactUnprocessable(mtdId)

          When(s"I call PUT /customer-facts/update/$mtdId")
          val res = IncomeTaxViewChange.putUpdateCustomerFacts(mtdId)

          HipUpdateCustomerFactStub.verifyPutUpdateCustomerFact(mtdId)

          res should have(
            httpStatus(UNPROCESSABLE_ENTITY)
          )
        }
      }

      "HIP returns 500" should {
        "return 500" in {
          isAuthorised(true)

          HipUpdateCustomerFactStub.stubPutUpdateCustomerFactServerError(mtdId)

          When(s"I call PUT /customer-facts/update/$mtdId")
          val res = IncomeTaxViewChange.putUpdateCustomerFacts(mtdId)

          HipUpdateCustomerFactStub.verifyPutUpdateCustomerFact(mtdId)

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )
        }
      }

      "HIP returns 503" should {
        "return 500 mapped from 503" in {
          isAuthorised(true)

          HipUpdateCustomerFactStub.stubPutUpdateCustomerFactServiceUnavailable(mtdId)

          When(s"I call PUT /customer-facts/update/$mtdId")
          val res = IncomeTaxViewChange.putUpdateCustomerFacts(mtdId)

          HipUpdateCustomerFactStub.verifyPutUpdateCustomerFact(mtdId)

          res should have(
            httpStatus(INTERNAL_SERVER_ERROR)
          )
        }
      }
    }

    "unauthorised" should {
      "return 401" in {
        isAuthorised(false)

        When(s"I call PUT /customer-facts/update/$mtdId")
        val res = IncomeTaxViewChange.putUpdateCustomerFacts(mtdId)

        res should have(
          httpStatus(UNAUTHORIZED),
          emptyBody
        )
      }
    }
  }
}