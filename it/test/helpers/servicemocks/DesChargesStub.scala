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

package helpers.servicemocks

import play.api.libs.json.{JsValue, Json}
import helpers.WiremockHelper

object DesChargesStub {

  private def detailsUrl(nino: String, from: String, to: String): String = {
    s"/enterprise/02.00.00/financial-data/NINO/$nino/ITSA?dateFrom=$from&dateTo=$to&onlyOpenItems=false&includeLocks=true&calculateAccruedInterest=true&removePOA=false&customerPaymentInformation=true&includeStatistical=false"
  }

  private def singleDocumentDetailsUrl(nino: String, documentId: String): String = {
    s"/enterprise/02.00.00/financial-data/NINO/$nino/ITSA?docNumber=$documentId&onlyOpenItems=false&includeLocks=true&calculateAccruedInterest=true&removePOA=false&customerPaymentInformation=true&includeStatistical=false"
  }

  private def onlyOpenItemsUrl(nino: String): String = {
    s"/enterprise/02.00.00/financial-data/NINO/$nino/ITSA?onlyOpenItems=true&includeLocks=true&calculateAccruedInterest=true&removePOA=false&customerPaymentInformation=true&includeStatistical=false"
  }

  private def historyUrl(mtdBsa: String, chargeReference: String): String = {
    s"/cross-regime/charges/MTDBSA/$mtdBsa/ITSA?chargeReference=$chargeReference"
  }

  private def repaymentHistoryByIdUrl(nino: String, repaymentId: String): String = {
    s"/income-tax/self-assessment/repayments-viewer/$nino?repaymentRequestNumber=$repaymentId"
  }

  private def allRepaymentHistoryUrl(nino: String): String = {
    s"/income-tax/self-assessment/repayments-viewer/$nino"
  }

  def stubGetChargeDetails(nino: String, from: String, to: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    stubGetChargeDetails(nino, from, to, status, response.toString)
  }

  def stubGetChargeDetails(nino: String, from: String, to: String, status: Int, responseBody: String): Unit = {
    WiremockHelper.stubGet(detailsUrl(nino, from, to), status, responseBody)
  }

  def stubGetSingleDocumentDetails(nino: String, documentId: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    WiremockHelper.stubGet(singleDocumentDetailsUrl(nino, documentId), status, response.toString)
  }

  def stubGetOnlyOpenItems(nino: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    stubGetOnlyOpenItems(nino, status, response.toString)
  }

  def stubGetOnlyOpenItems(nino: String, status: Int, responseBody: String): Unit = {
    WiremockHelper.stubGet(onlyOpenItemsUrl(nino), status, responseBody)
  }

  def stubChargeHistory(mtdBsa: String, documentId: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    WiremockHelper.stubGet(historyUrl(mtdBsa, documentId), status, response.toString())
  }

  def stubRepaymentHistoryById(nino: String, repaymentId: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    WiremockHelper.stubGet(repaymentHistoryByIdUrl(nino, repaymentId), status, response.toString())
  }

  def stubAllRepaymentHistory(nino: String)(status: Int, response: JsValue = Json.obj()): Unit = {
    WiremockHelper.stubGet(allRepaymentHistoryUrl(nino), status, response.toString())
  }
}
