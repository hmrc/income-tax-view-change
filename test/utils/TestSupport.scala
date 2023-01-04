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

package utils

import config.MicroserviceAppConfig
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait TestSupport extends WordSpecLike with Matchers with OptionValues
  with GuiceOneServerPerSuite with BeforeAndAfterAll with MockitoSugar with MaterializerSupport with ScalaFutures {
  this: Suite =>

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  implicit val hc: HeaderCarrier = HeaderCarrier()

  val microserviceAppConfig: MicroserviceAppConfig = app.injector.instanceOf[MicroserviceAppConfig]

}
