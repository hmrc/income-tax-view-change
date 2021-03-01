/*
 * Copyright 2021 HM Revenue & Customs
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

package mocks

import connectors.OutStandingChargesConnector
import connectors.httpParsers.OutStandingChargesHttpParser.OutStandingChargeResponse
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

trait MockOutStandingChargesConnector extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  val mockOutStandingChargesConnector: OutStandingChargesConnector = mock[OutStandingChargesConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockOutStandingChargesConnector)
  }

  def mockListOutStandingCharges(idType: String, idNumber: Int, taxYearEndDate: String)
                     (response: OutStandingChargeResponse): Unit = {
    when(mockOutStandingChargesConnector.listOutStandingCharges(
      idType = ArgumentMatchers.eq(idType),
      idNumber = ArgumentMatchers.eq(idNumber),
      taxYearEndDate = ArgumentMatchers.eq(taxYearEndDate)
    )(ArgumentMatchers.any())) thenReturn Future.successful(response)
  }
}
