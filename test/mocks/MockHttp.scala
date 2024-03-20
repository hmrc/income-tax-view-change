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

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.matches
import org.mockito.Mockito.{mock, reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import uk.gov.hmrc.http.{HttpClient, HttpReads, HttpResponse}

import scala.concurrent.Future


trait MockHttp extends AnyWordSpecLike with Matchers with OptionValues with BeforeAndAfterEach {

  val mockHttpGet: HttpClient = mock(classOf[HttpClient])

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttpGet)
  }

  def mockDesGet[A, B](url: String, queryParameters: Seq[(String, String)], headers: Seq[(String, String)])(response: Either[A, B]): Unit = {
    when(mockHttpGet.GET[Either[A, B]](
      matches(url),
      ArgumentMatchers.eq(queryParameters),
      ArgumentMatchers.eq(headers)
    )(ArgumentMatchers.any(),
      ArgumentMatchers.any(),
      ArgumentMatchers.any())
    ).thenReturn(Future.successful(response))
  }

  def setupMockHttpFutureGet[A](url: String)(response: A): OngoingStubbing[Future[A]] =
    when(mockHttpGet.GET[A](matches(url), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any[HttpReads[A]](), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockHttpGet(url: String)(response: HttpResponse): Unit =
    when(mockHttpGet.GET[HttpResponse](matches(url), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockHttpGetFailed(url: String): Unit =
    when(mockHttpGet.GET[HttpResponse](matches(url), ArgumentMatchers.any(), ArgumentMatchers.any())
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.failed(new Exception("error")))

  def setupMockHttpGetWithHeaderCarrier(url: String, headers: Seq[(String, String)])(response: HttpResponse): Unit =
    when(mockHttpGet.GET[HttpResponse](ArgumentMatchers.eq(url), ArgumentMatchers.any(), ArgumentMatchers.eq(headers))
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockHttpPutWithHeaderCarrier[R](url: String, headers: Seq[(String, String)])(body: R,response: HttpResponse): Unit =
    when(mockHttpGet.PUT[R,HttpResponse](ArgumentMatchers.eq(url), ArgumentMatchers.eq(body), ArgumentMatchers.eq(headers))
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockHttpPutFailed[R](url: String, headers: Seq[(String, String)])(body: R): Unit =
    when(mockHttpGet.PUT[R,HttpResponse](ArgumentMatchers.eq(url), ArgumentMatchers.eq(body), ArgumentMatchers.eq(headers))
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Future.failed(new Exception("error")))

  def setupMockHttpPostWithHeaderCarrier[R](url: String, headers: Seq[(String, String)])(body: R, response: HttpResponse): Unit =
    when(mockHttpGet.POST[R, HttpResponse](ArgumentMatchers.eq(url), ArgumentMatchers.eq(body), ArgumentMatchers.eq(headers))
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockHttpPostFailed[R](url: String, headers: Seq[(String, String)])(body: R): Unit =
    when(mockHttpGet.POST[R, HttpResponse](ArgumentMatchers.eq(url), ArgumentMatchers.eq(body), ArgumentMatchers.eq(headers))
      (ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.failed(new Exception("error")))
}
