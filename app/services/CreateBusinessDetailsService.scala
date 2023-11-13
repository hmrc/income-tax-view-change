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

package services

import connectors.CreateBusinessDetailsConnector
import models.incomeSourceDetails.CreateBusinessDetailsResponseModel.{CreateBusinessDetailsErrorResponse, IncomeSource}
import models.incomeSourceDetails.CreateIncomeSourceRequest
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class CreateBusinessDetailsService @Inject()(createBusinessDetailsConnector: CreateBusinessDetailsConnector) extends Logging {

  def createBusinessDetails(mtdbsaRef: String, createIncomeSourceRequest: CreateIncomeSourceRequest)
                           (implicit headerCarrier: HeaderCarrier): Future[Either[CreateBusinessDetailsErrorResponse, List[IncomeSource]]] = {
    logger.debug(s"[CreateBusinessDetailsController][createBusinessDetails] - $createIncomeSourceRequest")
    createBusinessDetailsConnector.create(mtdbsaRef, createIncomeSourceRequest)
  }
}
