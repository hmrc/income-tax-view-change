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

package mocks

import connectors.{PaymentAllocationsConnector, UpdateIncomeSourceConnector}
import connectors.httpParsers.PaymentAllocationsHttpParser.PaymentAllocationsResponse
import models.updateIncomeSource.UpdateIncomeSourceResponse
import models.updateIncomeSource.request.UpdateIncomeSourceRequestModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, Matchers, OptionValues, WordSpecLike}
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

trait MockUpdateIncomeSourceConnector extends WordSpecLike with Matchers with OptionValues with MockitoSugar with BeforeAndAfterEach {

  val mockUpdateIncomeSourceConnector: UpdateIncomeSourceConnector = mock[UpdateIncomeSourceConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUpdateIncomeSourceConnector)
  }

  def mockUpdateIncomeSource(request:UpdateIncomeSourceRequestModel)
                               (response: UpdateIncomeSourceResponse): Unit = {
    when(mockUpdateIncomeSourceConnector.updateIncomeSource(ArgumentMatchers.eq(request))(ArgumentMatchers.any())) thenReturn Future.successful(response)
  }

}
