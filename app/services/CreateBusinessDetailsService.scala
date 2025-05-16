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

import config.MicroserviceAppConfig
import connectors.CreateBusinessDetailsConnector
import connectors.hip.CreateBusinessDetailsHipConnector
import models.createIncomeSource._
import models.hip.CreateIncomeSourceHipApi
import models.hip.createIncomeSource._
import models.hip.incomeSourceDetails.{CreateBusinessDetailsHipErrorResponse, IncomeSource}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateBusinessDetailsService @Inject()(
                                              createBusinessDetailsConnector: CreateBusinessDetailsConnector,
                                              createBusinessDetailsHipConnector: CreateBusinessDetailsHipConnector,
                                              microserviceAppConfig: MicroserviceAppConfig
                                            ) extends Logging {

  def createBusinessDetails(mtdbsaRef: String, body: CreateIncomeSourceRequest)
                           (implicit headerCarrier: HeaderCarrier,
                            executionContext: ExecutionContext): Future[Either[CreateBusinessDetailsHipErrorResponse, List[IncomeSource]]] = {
    if(microserviceAppConfig.hipFeatureSwitchEnabled(CreateIncomeSourceHipApi)) {
      val requestModel: CreateIncomeSourceHipRequest = body match {
        case CreateBusinessIncomeSourceRequest(bd) => CreateBusinessIncomeSourceHipRequest(mtdbsaRef, bd.map(_.toHipModel))
        case CreateForeignPropertyIncomeSourceRequest(fp) => CreateForeignPropertyIncomeSourceHipRequest(mtdbsaRef, fp.toHipModel)
        case CreateUKPropertyIncomeSourceRequest(ukp) => CreateUKPropertyIncomeSourceHipRequest(mtdbsaRef, ukp.toHipModel)
        //this case should never be invoked but have to add it due to compile warning. This should be removed during code cleanup of hip migration.
        case _ => throw new Exception("Unexpected requestModel")
      }
      createBusinessDetailsHipConnector.create(requestModel)
    } else {
      createBusinessDetailsConnector.create(mtdbsaRef, body).map {
        case Right(listOfIncomeSources) => Right(listOfIncomeSources.map(_.toHipModel))
        case Left(error) => Left(error.toHipModel)
      }
    }
  }
}
