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

package services

import connectors.httpParsers.ChargeHttpParser.{ChargeResponse, ChargeResponseError}
import connectors.{CalculationListConnector, ChargeHistoryDetailsConnector, FinancialDetailsConnector}
import models.claimToAdjustPoa.PaymentOnAccountViewModel
import models.financialDetails.responses.ChargesResponse
import services.helpers.ClaimToAdjustHelper
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ClaimToAdjustService @Inject()(val financialDetailsConnector: FinancialDetailsConnector,
                                     val chargeHistoryConnector: ChargeHistoryDetailsConnector,
                                     val calculationListConnector: CalculationListConnector,
                                     implicit val dateService: DateServiceInterface)
                                    (implicit ec: ExecutionContext) extends ClaimToAdjustHelper {

  def getPoaTaxYearForEntryPoint(nino: String)
                                (implicit hc: HeaderCarrier): Future[Either[Throwable, Option[Int]]] = {
    for {
      res <- getNonCrystallisedFinancialDetails(nino)
    } yield res match {
      case Right(financialDetails: ChargesResponse) =>
        val x = arePoAPaymentsPresent(financialDetails.documentDetails)
        Right(x)
      case Right(None) => Right(None)
      case Left(ex) =>
//        Logger("application").error(s"There was an error getting FinancialDetailsModel" +
//          s" < cause: ${ex.getCause} message: ${ex.getMessage} >")
        Left(ex)
    }
  }

  def getPoaForNonCrystallisedTaxYear(nino: String)(implicit hc: HeaderCarrier): Future[Either[Throwable, Option[PaymentOnAccountViewModel]]] = {
    for {
      res <- getNonCrystallisedFinancialDetails(nino)
    } yield res match {
      case Right(financialDetails: ChargesResponse) =>
        val x = getPaymentOnAccountModel(sortByTaxYear(financialDetails.documentDetails))
        Right(x)
      case Right(None) => Right(None)
      case Left(ex) => Left(ex)
    }
  }

//  def getPoaViewModelWithAdjustmentReason(nino: String, taxYear: List[Int])
//                                         (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Throwable, PaymentOnAccountViewModel]] = {
//    for {
//      finanicalAndPoaModelMaybe <- getPoaModelAndFinancialDetailsForNonCrystallised(nino, taxYear)
//      adjustmentReasonMaybe     <- getPoaAdjustmentReason(nino, finanicalAndPoaModelMaybe)
//    } yield (adjustmentReasonMaybe, finanicalAndPoaModelMaybe) match {
//      case (Right(reason), Right(FinancialDetailsAndPoAModel(_, Some(model)))) =>
//        Right(
//          model.copy(previouslyAdjusted = Some(reason.isDefined))
//        )
//      case (Left(ex), _) => Left(ex)
//      case (_, Left(ex)) => Left(ex)
//      case _ => Left(new Exception("Unexpected error when creating Enter PoA Amount view model"))
//    }
//  }

//
//  def getAmendablePoaViewModel(nino: Nino)
//                              (implicit hc: HeaderCarrier, user: MtdItUser[_]): Future[Either[Throwable, PaymentOnAccountViewModel]] = {
//    getNonCrystallisedFinancialDetails(nino)
//      .flatMap  {
//        case Right(Some(FinancialDetailsModel(_, documentDetails, FinancialDetail(_, _, _, _, _, chargeReference, _, _, _, _, _, _, _, _) :: _))) =>
//          isSubsequentAdjustment(chargeHistoryConnector, chargeReference)
//            .map {
//              case Right(haveBeenAdjusted) => getAmendablePoaViewModel(sortByTaxYear(documentDetails), haveBeenAdjusted)
//              case Left(ex)                => Left(ex)
//            }
//        case Right(_) => Future.successful(Left(new Exception("Failed to retrieve non-crystallised financial details")))
//        case Left(ex) => Future.successful(Left(ex))
//      }
//  }
//
//


  private def getPoaAdjustmentReason(nino: String, financialPoaDetails:  ChargeResponse)
                                    (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Throwable, Option[String]]] = {
    financialPoaDetails match {
      case Right(finDetails) =>
        finDetails.financialDetails.headOption match {
          case Some(detail) => getChargeHistory(nino, chargeHistoryConnector, detail.chargeReference.get) map {
            case Right(chargeHistory) =>
              Right( chargeHistory.chargeHistoryDetails.flatMap(x => x.headOption.flatMap(_.poaAdjustmentReason)) )
            case Right(_) => Right(None)
            case Left(_) => Left(new RuntimeException("Some error"))
          }
          case None => Future.successful(Left(new Exception("No financial details found for this charge")))
        }
      //case Right(_) => Future.successful(Right(None))
      //case Left(ex) => Future.successful(Left(ex))
    }
  }

  private def getNonCrystallisedFinancialDetails(nino: String)
                                                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[Throwable, Option[ChargeResponse]]] = {
//    checkCrystallisation(nino, getPoaAdjustableTaxYears)(nino, calculationListConnector).flatMap {
//      case None => Future.successful(Right(None))
//      case Some(taxYear) => financialDetailsConnector.getChargeDetails(nino, taxYear.toString, (taxYear + 1).toString) ).map {
//        case financialDetails: FinancialDetailsModel => Right(Some(financialDetails))
//        case error: FinancialDetailsErrorModel if error.code != NOT_FOUND => Left(new Exception("There was an error whilst fetching financial details data"))
//        case _ => Right(None)
//      }
//    }
    ???
  }

    private def getPoaModelAndFinancialDetailsForNonCrystallised(nino: String, taxYear: List[Int])
                                                              (implicit hc: HeaderCarrier): Future[Either[Throwable, FinancialDetailsAndPoAModel]] = {
    checkCrystallisation(nino, getPoaAdjustableTaxYears)(hc, calculationListConnector, ec).flatMap {
      case None =>
        Future.successful(Right(FinancialDetailsAndPoAModel(None, None)))
      case Some(taxYear) =>
        financialDetailsConnector.getChargeDetails(nino, taxYear.toString, (taxYear + 1).toString ).map {
          case Right(financialDetails: ChargesResponse) =>
            Right(FinancialDetailsAndPoAModel(Some(financialDetails), getPaymentOnAccountModel(sortByTaxYear(financialDetails.documentDetails))))
          case Left(_) =>
//          if error.code != NOT_FOUND =>
            Left(new Exception("There was an error whilst fetching financial details data"))
          case _ =>
            Right(FinancialDetailsAndPoAModel(None, None))
        }
    }
  }

   case class FinancialDetailsAndPoAModel(financialDetails: Option[ChargesResponse],
                                                 poaModel: Option[PaymentOnAccountViewModel])
}