/*
 * Copyright 2024 HM Revenue & Customs
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

package services.helpers


import connectors.httpParsers.ChargeHistoryHttpParser.ChargeHistoryResponse
import connectors.{CalculationListConnector, ChargeHistoryDetailsConnector}
import models.claimToAdjustPoa.PaymentOnAccountViewModel
import models.financialDetails.DocumentDetail
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier

import java.time.{LocalDate, Month}
import scala.concurrent.{ExecutionContext, Future}

// TODO: This part of the logic expected to be moved within BE
// TODO: plain models like: TaxYear and PaymentOnAccountViewModel will be return via new connector
trait ClaimToAdjustHelper {

  private val POA1: String = "ITSA- POA 1"
  private val POA2: String = "ITSA - POA 2"

  private val LAST_DAY_OF_JANUARY: Int = 31

  protected val poaDocumentDescriptions: List[String] = List(POA1, POA2)

  private val isPoAOne: DocumentDetail => Boolean = documentDetail =>
    documentDetail.documentDescription.contains(POA1)

  private val isPoATwo: DocumentDetail => Boolean = documentDetail =>
    documentDetail.documentDescription.contains(POA2)

  private val isPoA: DocumentDetail => Boolean = documentDetail =>
    isPoAOne(documentDetail) || isPoATwo(documentDetail)

  private val getTaxReturnDeadline: LocalDate => LocalDate = date =>
    LocalDate.of(date.getYear, Month.JANUARY, LAST_DAY_OF_JANUARY)
      .plusYears(1)

  val sortByTaxYear: List[DocumentDetail] => List[DocumentDetail] =
    _.sortBy(_.taxYear).reverse

  def hasPartiallyOrFullyPaidPoas(documentDetails: List[DocumentDetail]): Boolean =
    documentDetails.exists(isPoA) &&
      (documentDetails.exists(_.isPartPaid) || documentDetails.exists(_.isPaid))

  def getPaymentOnAccountModel(documentDetails: List[DocumentDetail], poaPreviouslyAdjusted: Option[Boolean] = None): Option[PaymentOnAccountViewModel] = for {
    poaOneDocDetail         <- documentDetails.find(isPoAOne)
    poaTwoDocDetail         <- documentDetails.find(isPoATwo)
    latestDocumentDetail     = poaTwoDocDetail
    poaTwoDueDate           <- poaTwoDocDetail.documentDueDate
    taxReturnDeadline        = getTaxReturnDeadline(poaTwoDueDate)
    poasAreBeforeDeadline    = poaTwoDueDate isBefore taxReturnDeadline
    if poasAreBeforeDeadline
  } yield
    PaymentOnAccountViewModel(
      poaOneTransactionId   = poaOneDocDetail.transactionId,
      poaTwoTransactionId   = poaTwoDocDetail.transactionId,
      taxYear               = latestDocumentDetail.taxYear,
      totalAmountOne   = poaOneDocDetail.originalAmount,
      totalAmountTwo   = poaTwoDocDetail.originalAmount,
      relevantAmountOne  = poaOneDocDetail.poaRelevantAmount.getOrElse(throw new RuntimeException("DocumentDetail.poaRelevantAmount")),
      relevantAmountTwo  = poaTwoDocDetail.poaRelevantAmount.getOrElse(throw new RuntimeException("DocumentDetail.poaRelevantAmount")),
      previouslyAdjusted = poaPreviouslyAdjusted,
      partiallyPaid      = poaOneDocDetail.isPartPaid || poaTwoDocDetail.isPartPaid,
      fullyPaid = poaOneDocDetail.isPaid || poaTwoDocDetail.isPaid
    )

  protected def getChargeHistory(nino: String, chargeHistoryConnector: ChargeHistoryDetailsConnector, chargeReference: String)
                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ChargeHistoryResponse] = ???
//    chargeHistoryConnector.getChargeHistoryDetails(nino, chargeReference).map {
//      case ChargesHistoryModel(_, _, _, chargeHistoryDetails) => chargeHistoryDetails match {
//        case Some(detailsList) => Right(detailsList.headOption)
//        case None => Right(None)
//      }
//      case ChargesHistoryErrorModel(code, message) =>
//        Logger("application").error("chargeHistoryConnector.getChargeHistory returned a non-valid response")
//        Left(new Exception(s"Error retrieving charge history code: $code message: $message"))
//    }

  protected def isTaxYearNonCrystallised(taxYear: Int, nino: String)
                                        (implicit hc: HeaderCarrier,
                                         calculationListConnector: CalculationListConnector, ec: ExecutionContext): Future[Boolean] = {
    //TODO: identify future TaxYear
    //if (taxYear.isFutureTaxYear(dateService)) {
    //  Future.successful(true)
    //} else {
      calculationListConnector.getCalculationList(nino, taxYear.toString).flatMap {
        case Right(res) => Future.successful(res.calculations.head.crystallised.getOrElse(false))
        //case err: CalculationListErrorModel if err.code == 404 =>
        //  Logger("application").info("User had no calculations for this tax year, therefore is non-crystallised")
        //  Future.successful(false)
        //case err: CalculationListErrorModel =>
         // Logger("application").error("getCalculationList returned a non-valid response")
        //  Future.failed(new InternalServerException(err.message))
      }.map(!_)
    //}
  }

  protected def checkCrystallisation(nino: String, taxYearList: List[Int])
                                    (implicit hc: HeaderCarrier,
                                     calculationListConnector: CalculationListConnector, ec: ExecutionContext): Future[Option[Int]] = {
    taxYearList.foldLeft(Future.successful(Option.empty[Int])) { (acc, item) =>
      acc.flatMap {
        case Some(_) => acc
        case None => isTaxYearNonCrystallised(item, nino)(hc, calculationListConnector, ec) map {
          case true => Some(item)
          case false => None
        }
      }
    }
  }

  // TODO: TaxYear list for POA would need to be provided by FE?
  protected def getPoaAdjustableTaxYears(): List[Int] = {
    List.empty
//    if (dateService.isAfterTaxReturnDeadlineButBeforeTaxYearEnd) {
//      List(
//        TaxYear.makeTaxYearWithEndYear(dateService.getCurrentTaxYearEnd)
//      )
//    } else {
//      List(
//        TaxYear.makeTaxYearWithEndYear(dateService.getCurrentTaxYearEnd).addYears(-1),
//        TaxYear.makeTaxYearWithEndYear(dateService.getCurrentTaxYearEnd)
//      ).sortBy(_.endYear)
//    }
  }

  protected def arePoAPaymentsPresent(documentDetails: List[DocumentDetail]): Option[Int] = {
    documentDetails.filter(_.documentDescription.exists(description => poaDocumentDescriptions.contains(description)))
      .sortBy(_.taxYear).reverse.headOption.map(doc => doc.taxYear)
  }

  def getAmendablePoaViewModel(documentDetails: List[DocumentDetail],
                               poasHaveBeenAdjustedPreviously: Boolean): Either[Throwable, PaymentOnAccountViewModel] = (for {
    poaOneDocDetail         <- documentDetails.find(isPoAOne)
    poaTwoDocDetail         <- documentDetails.find(isPoATwo)
    latestDocumentDetail     = poaTwoDocDetail
    poaTwoDueDate           <- poaTwoDocDetail.documentDueDate
    taxReturnDeadline        = getTaxReturnDeadline(poaTwoDueDate)
    poasAreBeforeDeadline    = poaTwoDueDate isBefore taxReturnDeadline
    if poasAreBeforeDeadline
  } yield {
    PaymentOnAccountViewModel(
      poaOneTransactionId            = poaOneDocDetail.transactionId,
      poaTwoTransactionId            = poaTwoDocDetail.transactionId,
      taxYear                        = latestDocumentDetail.taxYear,
      totalAmountOne            = poaOneDocDetail.originalAmount,
      totalAmountTwo            = poaTwoDocDetail.originalAmount,
      relevantAmountOne           = poaOneDocDetail.poaRelevantAmount.getOrElse(throw new RuntimeException("DocumentDetail.poaRelevantAmount")),
      relevantAmountTwo           = poaTwoDocDetail.poaRelevantAmount.getOrElse(throw new RuntimeException("DocumentDetail.poaRelevantAmount")),
      //TODO: create methods => isPartPaid and isPaid for => DocumentDetail
      partiallyPaid               = false, //poaOneDocDetail.isPartPaid || poaTwoDocDetail.isPartPaid,
      fullyPaid                   = false, //poaOneDocDetail.isPaid || poaTwoDocDetail.isPaid,
      previouslyAdjusted = Some(poasHaveBeenAdjustedPreviously)
    )
  }) match {
    case Some(model) => Right(model)
    case None        =>
      Logger("application").error("Failed to create AmendablePoaViewModel")
      Left(new Exception("Failed to create AmendablePoaViewModel"))
  }

  // TODO: implement BE leve manipulation with 1554: +
//  protected def isSubsequentAdjustment(chargeHistoryConnector: ChargeHistoryDetailsConnector, chargeReference: String)
//                                      (implicit hc: HeaderCarrier, nino: String, ec: ExecutionContext): Future[Either[Throwable, Boolean]] = {
//    chargeHistoryConnector.getChargeHistoryDetails(nino, chargeReference) map {
//      case ChargeHistoryResponse(_, _, _, Some(charges)) if charges.filter(_.isPoA).exists(_.poaAdjustmentReason.isDefined) => Right(true)
//      case ChargesHistoryModel(_, _, _, _)                                                                                => Right(false)
//      case ChargesHistoryErrorModel(code, message) =>
//        Logger("application").error("getChargeHistory returned a non-valid response")
//        Left(new Exception(s"Error retrieving charge history code: $code message: $message"))
//    }
//    Future.successful( Right(true) )
//  }

}