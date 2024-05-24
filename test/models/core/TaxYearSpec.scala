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

package models.core

import org.scalatest.matchers.should.Matchers
import utils.TestSupport

class TaxYearSpec extends TestSupport with Matchers {

  "currentTaxYearMinusOne method" when {
    "invoked on a TaxYear object" should {
      "return a new TaxYear object where the years are one year less than before" in {
        val taxYear: TaxYear = TaxYear(2098, 2099)
        val taxYearMinusOne = taxYear.addYears(-1)

        val desiredTaxYearObject: TaxYear = TaxYear(2097, 2098)

        taxYearMinusOne shouldBe desiredTaxYearObject
      }
      "return a new TaxYear object where the years are 100 years less than before" in {
        val taxYear: TaxYear = TaxYear(2098, 2099)
        val taxYearMinusOne = taxYear.addYears(-100)

        val desiredTaxYearObject: TaxYear = TaxYear(1998, 1999)

        taxYearMinusOne shouldBe desiredTaxYearObject
      }
    }
  }

  "currentTaxYearPlusOne method" when {
    "invoked on a TaxYear object" should {
      "return a new TaxYear object where the years are one year more than before" in {
        val taxYear: TaxYear = TaxYear(2098, 2099)
        val taxYearMinusOne = taxYear.addYears(1)

        val desiredTaxYearObject: TaxYear = TaxYear(2099, 2100)

        taxYearMinusOne shouldBe desiredTaxYearObject
      }
      "return a new TaxYear object where the years are one hundred years more than before" in {
        val taxYear: TaxYear = TaxYear(2098, 2099)
        val taxYearMinusOne = taxYear.addYears(100)

        val desiredTaxYearObject: TaxYear = TaxYear(2198, 2199)

        taxYearMinusOne shouldBe desiredTaxYearObject
      }
    }
  }

  "formatTaxYearRange method" when {
    "invoked on a TaxYear object" should {
      "return a string with the tax year range" in {
        val taxYear: TaxYear = TaxYear(2098, 2099)
        val taxYearRange: String = taxYear.formatTaxYearRange

        val desiredTaxYearRangeString: String = "98-99"

        taxYearRange shouldBe desiredTaxYearRangeString
      }
    }
  }

  "TaxYear.getTaxYearStartYearEndYear" when {
    "given an input of letters with the correct length" should {
      "not return a TaxYear model" in {
        TaxYear.fromString("ABCD-EFGH").isDefined shouldBe false
      }
    }
    "given an input of letters with the incorrect length" should {
      "not return a TaxYear model" in {
        TaxYear.fromString("ABCDEFGH-IJKLMNO").isDefined shouldBe false
      }
    }
    "given an input of numbers with more than one dash" should {
      "not return a TaxYear model" in {
        TaxYear.fromString("2020-2021-2022").isDefined shouldBe false
      }
    }
    "given an empty input" should {
      "not return a TaxYear model" in {
        TaxYear.fromString("").isDefined shouldBe false
      }
    }
    "given an input with no dashes" should {
      "not return a TaxYear model" in {
        TaxYear.fromString("20212023").isDefined shouldBe false
      }
    }
    "given an input with years in the incorrect format" should {
      "not return a TaxYear model" in {
        TaxYear.fromString("21-22").isDefined shouldBe false
      }
    }
    "given an input with years which have length greater than 4" should {
      "not return a TaxYear model" in {
        TaxYear.fromString("20221-20222").isDefined shouldBe false
      }
    }
    "given an input with numerical years in the format YYYY-YYYY with a numerical difference of 2" should {
      "not return a TaxYear model" in {
        TaxYear.fromString("2020-2022").isDefined shouldBe false
      }
    }
    "given an input where yearOne is greater than yearTwo" should {
      "not return a TaxYear model" in {
        TaxYear.fromString("2022-2021").isDefined shouldBe false
      }
    }
    "given an input with numerical years in the format YYYY-YYYY with a numerical difference of 1" should {
      "return a TaxYear model" in {
        TaxYear.fromString("2021-2022").isDefined shouldBe true
      }
    }
  }

  "TaxYear constructor" should {
    "return 2023-2024" when {
      "constructor" in {
        val forYearStart = 2023
        val forYearEnd = 2024
        TaxYear(forYearStart, forYearEnd).toString shouldBe "2023-2024"
      }
    }
  }

  "toString method" should {
    "return 2023-2024" when {
      "toString" in {
        val forYearEnd = 2024
        TaxYear.forYearEnd(forYearEnd).toString shouldBe "2023-2024"
      }
    }
  }

  "previousYear method" should {
    "return 2022-2023" when {
      "previousYear" in {
        val forYearEnd = 2024
        TaxYear.forYearEnd(forYearEnd).previousYear.toString shouldBe "2022-2023"
      }
    }
  }

  "nextYear method" should {
    "return 2024-2025" when {
      "nextYear" in {
        val forYearEnd = 2024
        TaxYear.forYearEnd(forYearEnd).nextYear.toString shouldBe "2024-2025"
      }
    }
  }

  "isSameAs method" should {
    "return true" when {
      "the TaxYear object isSameAs given tax year" in {
        TaxYear.forYearEnd(2024).isSameAs(TaxYear.forYearEnd(2024)) shouldBe true
      }
    }
    "return false" when {
      "the TaxYear object is not same as given tax year" in {
        TaxYear.forYearEnd(2024).isSameAs(TaxYear.forYearEnd(2023)) shouldBe false
      }
    }
  }

  "isBefore method" should {
    "return true" when {
      "the TaxYear object isSameAs given tax year" in {
        TaxYear.forYearEnd(2023).isBefore(TaxYear.forYearEnd(2024)) shouldBe true
      }
    }
    "return false" when {
      "the TaxYear object is not before given tax year" in {
        TaxYear.forYearEnd(2024).isBefore(TaxYear.forYearEnd(2023)) shouldBe false
      }
    }
  }

  "isAfter method" should {
    "return true" when {
      "the TaxYear object isAfter given tax year" in {
        TaxYear.forYearEnd(2024).isAfter(TaxYear.forYearEnd(2023)) shouldBe true
      }
    }
    "return false" when {
      "the TaxYear object is not after given tax year" in {
        TaxYear.forYearEnd(2024).isAfter(TaxYear.forYearEnd(2024)) shouldBe false
      }
    }
  }


}
