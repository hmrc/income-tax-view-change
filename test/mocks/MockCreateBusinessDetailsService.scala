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

import models.hip.incomeSourceDetails.{CreateBusinessDetailsHipErrorResponse, IncomeSource}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.http.Status.INTERNAL_SERVER_ERROR
import services.CreateBusinessDetailsService

import scala.concurrent.Future


trait MockCreateBusinessDetailsService extends AnyWordSpecLike with Matchers with OptionValues  with BeforeAndAfterEach {

  val mockCreateBusinessDetailsService: CreateBusinessDetailsService = mock(classOf[CreateBusinessDetailsService])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCreateBusinessDetailsService)
  }

  val testIncomeSourceId: String = "AAIS12345678901"

  def mockCreateBusinessDetailsErrorResponse(): OngoingStubbing[Future[Either[CreateBusinessDetailsHipErrorResponse, List[IncomeSource]]]] =
    when(mockCreateBusinessDetailsService.createBusinessDetails(any())(any()))
      .thenReturn(
        Future.successful(
          Left(
            CreateBusinessDetailsHipErrorResponse(INTERNAL_SERVER_ERROR, "failed to create income source")
          )
        )
      )

  def mockCreateIncomeSourceSuccessResponse(): OngoingStubbing[Future[Either[CreateBusinessDetailsHipErrorResponse, List[IncomeSource]]]] =
    when(mockCreateBusinessDetailsService.createBusinessDetails(any())(any()))
      .thenReturn(
        Future.successful(
          Right(
            List(
              IncomeSource(testIncomeSourceId)
            )
          )
        )
      )
}
