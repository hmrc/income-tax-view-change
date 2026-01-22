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

package mocks

import connectors.hip.UpdateCustomerFactConnector
import models.hip.updateCustomerFact.UpdateCustomerFactResponseModel
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockUpdateCustomerFactConnector extends MockitoSugar {

  val mockUpdateCustomerFactConnector: UpdateCustomerFactConnector = mock[UpdateCustomerFactConnector]

  def mockUpdateCustomerFactsToConfirmed(mtdId: String, result: UpdateCustomerFactResponseModel): Unit =
    when(mockUpdateCustomerFactConnector.updateCustomerFactsToConfirmed(eqTo(mtdId))(any[HeaderCarrier]()))
      .thenReturn(Future.successful(result))
}
