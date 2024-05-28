/*
 * Copyright 2017 HM Revenue & Customs
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

package helpers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import com.github.tomakehurst.wiremock.http.{HttpHeader, HttpHeaders}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.{WSClient, WSRequest}

object WiremockHelper extends Eventually with IntegrationPatience {
  val wiremockPort = 11111
  val wiremockHost = "localhost"
  val url = s"http://$wiremockHost:$wiremockPort"

  def verifyPost(uri: String, body: String): Unit = {
    verify(postRequestedFor(urlEqualTo(uri)).withRequestBody(equalToJson(body)))
  }

  def verifyGet(uri: String): Unit = {
    verify(getRequestedFor(urlEqualTo(uri)))
  }

  def verifyPut(uri: String, requestBody: String): Unit = {
    verify(putRequestedFor(urlEqualTo(uri)).withRequestBody(equalToJson(requestBody)))
  }

  def stubGet(url: String, status: Integer, body: String): StubMapping =
    stubFor(get(urlEqualTo(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(body)
      )
    )

  def stubPost(url: String, status: Integer, responseBody: String): StubMapping =
    stubFor(post(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )

  def stubPost(url: String, status: Integer, requestBody: String, responseBody: String): StubMapping = {

    val x = stubFor(post(urlMatching(url)).withRequestBody(equalToJson(requestBody))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )

    println(s"\nStubPost returning: $x\n")

    stubFor(post(urlMatching(url)).withRequestBody(equalToJson(requestBody))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )
  }

  def stubPut(url: String, status: Integer, responseBody: String): StubMapping =
    stubFor(put(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )

  def stubPutWithHeaders(url: String, status: Int, responseBody: String, headers: Map[String, String] = Map.empty): StubMapping = {
    def toHttpHeaders(toConvert: Map[String, String]): HttpHeaders = {
      val headersList = toConvert.map { case (key, value) =>
        new HttpHeader(key, value)
      }.toSeq
      new HttpHeaders(headersList: _*)
    }

    stubFor(put(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody).
          withHeaders(toHttpHeaders(headers))
      )
    )
  }

  def stubPut(url: String, status: Integer, requestBody: String, responseBody: String): StubMapping = {

    val x = stubFor(put(urlMatching(url)).withRequestBody(equalToJson(requestBody))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )

    println(s"\nStubPut returning: $x\n")

    stubFor(put(urlMatching(url)).withRequestBody(equalToJson(requestBody))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )
  }

  def stubPatch(url: String, status: Integer, responseBody: String): StubMapping =
    stubFor(patch(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )

  def stubDelete(url: String, status: Integer, responseBody: String): StubMapping =
    stubFor(delete(urlMatching(url))
      .willReturn(
        aResponse().
          withStatus(status).
          withBody(responseBody)
      )
    )
}

trait WiremockHelper {
  self: GuiceOneServerPerSuite =>

  import WiremockHelper._

  lazy val ws = app.injector.instanceOf[WSClient]

  lazy val wmConfig = wireMockConfig().port(wiremockPort)
  lazy val wireMockServer = new WireMockServer(wmConfig)

  def startWiremock(): Unit = {
    wireMockServer.start()
    WireMock.configureFor(wiremockHost, wiremockPort)
  }

  def stopWiremock(): Unit = wireMockServer.stop()

  def resetWiremock(): Unit = WireMock.reset()

  def buildClient(path: String): WSRequest = {
    ws
      .url(s"http://localhost:$port/income-tax-view-change$path")
      .withHttpHeaders("Authorization" -> "Bearer123")
      .withFollowRedirects(false)
  }
}

