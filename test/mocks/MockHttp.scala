/*
 * Copyright 2018 HM Revenue & Customs
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
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future


trait MockHttp extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  val mockHttpGet: HttpClient = mock[HttpClient]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockHttpGet)
  }

  def setupMockHttpFutureGet[A](url: String)(response: A): OngoingStubbing[Future[A]] =
    when(mockHttpGet.GET[A](ArgumentMatchers.eq(url))(ArgumentMatchers.any(), ArgumentMatchers.any(),
      ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockHttpGet(url: String)(response: HttpResponse): Unit =
    when(mockHttpGet.GET[HttpResponse](ArgumentMatchers.eq(url))(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockHttpGetFailed(url: String): Unit =
    when(mockHttpGet.GET[HttpResponse](ArgumentMatchers.eq(url))(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Future.failed(new Exception("Error")))

  def setupMockHttpGetWithHeaderCarrier(url: String, hc: HeaderCarrier)(response: HttpResponse): Unit =
    when(mockHttpGet.GET[HttpResponse](ArgumentMatchers.eq(url))(ArgumentMatchers.any(), ArgumentMatchers.eq(hc), ArgumentMatchers.any())).thenReturn(Future.successful(response))
}
