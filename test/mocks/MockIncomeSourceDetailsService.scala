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

import constants.BaseTestConstants.mtdRef
import models.core.{NinoErrorModel, NinoModel}
import models.incomeSourceDetails.{IncomeSourceDetailsError, IncomeSourceDetailsModel}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{mock, reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Status
import services.IncomeSourceDetailsService

import scala.concurrent.Future


trait MockIncomeSourceDetailsService extends AnyWordSpecLike with Matchers with OptionValues  with BeforeAndAfterEach {

  val mockIncomeSourceDetailsService: IncomeSourceDetailsService = mock(classOf[IncomeSourceDetailsService])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIncomeSourceDetailsService)
  }

  def setupMockIncomeSourceDetailsResponse(mtdRef: String)(response: Result):
  OngoingStubbing[Future[Result]] = when(mockIncomeSourceDetailsService.getIncomeSourceDetails(
    ArgumentMatchers.eq(mtdRef))(ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockNinoResponse(mtdRef: String)(response: Result):
  OngoingStubbing[Future[Result]] = when(mockIncomeSourceDetailsService.getNino(
    ArgumentMatchers.eq(mtdRef))(ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def mockIncomeSourceDetailsResponse(desResponse: IncomeSourceDetailsModel):
  OngoingStubbing[Future[Result]] = setupMockIncomeSourceDetailsResponse(mtdRef)(Status(OK)(Json.toJson(desResponse)))

  def mockIncomeSourceDetailsErrorResponse(desResponse: IncomeSourceDetailsError):
  OngoingStubbing[Future[Result]] = setupMockIncomeSourceDetailsResponse(mtdRef)(Status(INTERNAL_SERVER_ERROR)(Json.toJson(desResponse)))

  def mockNinoResponse(desResponse: NinoModel):
  OngoingStubbing[Future[Result]] = setupMockNinoResponse(mtdRef)(Status(OK)(Json.toJson(desResponse)))

  def mockNinoErrorResponse(desResponse: NinoErrorModel):
  OngoingStubbing[Future[Result]] = setupMockNinoResponse(mtdRef)(Status(INTERNAL_SERVER_ERROR)(Json.toJson(desResponse)))
}
