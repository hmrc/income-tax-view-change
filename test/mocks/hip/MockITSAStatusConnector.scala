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

package mocks.hip

import connectors.hip.ITSAStatusConnector
import models.itsaStatus.{ITSAStatusResponse, ITSAStatusResponseModel}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, reset, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}

import scala.concurrent.Future

trait MockITSAStatusConnector extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {

  val mockHIPITSAStatusConnector: ITSAStatusConnector = mock(classOf[ITSAStatusConnector])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHIPITSAStatusConnector)
  }

  def mockHIPGetITSAStatus(response: Either[ITSAStatusResponse, List[ITSAStatusResponseModel]]): Unit = {
    when(mockHIPITSAStatusConnector.getITSAStatus(any, any, any, any)(ArgumentMatchers.any())) thenReturn Future.successful(response)
  }

}
