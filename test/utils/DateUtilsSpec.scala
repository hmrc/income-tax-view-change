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

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

class DateUtilsSpec extends TestSupport {

  "longDateTimestampGmt" should {
    "return Date/time in format [EEE, dd MMM yyyy HH:mm:ss z]" when {
      "given a LocalDateTime" in {
        val result: String = DateUtils.longDateTimestampGmt(
          LocalDateTime.parse("2023-01-17T12:00:00")
        )
        result shouldBe "Tue, 17 Jan 2023 12:00:00 GMT"
      }
    }
  }

  "nowAsUtc" should {
    "return Date/time in format [yyyy-MM-dd'T'HH:mm:ss'Z']" in {
      val result: String = DateUtils.nowAsUtc

      result should fullyMatch regex """\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z""".r

      ZonedDateTime.parse(result).getOffset shouldBe ZoneOffset.UTC
    }
  }

}
