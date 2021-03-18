/**
 * Copyright (C) the original author or authors.
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

package ninja.utils;

import org.junit.Test;

import static ninja.utils.TimeUtil.parseDuration;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class TimeUtilTest {

    @Test
    public void durationInDays() {
        assertEquals(0, parseDuration("0d"));
        assertEquals(86400, parseDuration("1d"));
        assertEquals(2592000, parseDuration("30d"));
    }

    @Test
    public void durationInHours() {
        assertEquals(0, parseDuration("0h"));
        assertEquals(3600, parseDuration("1h"));
        assertEquals(3600 * 24, parseDuration("24h"));
    }

    @Test
    public void durationInMinutes() {
        assertEquals(0, parseDuration("0mn"));
        assertEquals(0, parseDuration("0min"));
        assertEquals(60, parseDuration("1mn"));
        assertEquals(60, parseDuration("1min"));
        assertEquals(60 * 59, parseDuration("59mn"));
        assertEquals(60 * 59, parseDuration("59min"));
    }

    @Test
    public void durationInSeconds() {
        assertEquals(0, parseDuration("0s"));
        assertEquals(1, parseDuration("1s"));
        assertEquals(10, parseDuration("10s"));
        assertEquals(1234567890, parseDuration("1234567890s"));
    }

    @Test
    public void durationCannotBeNull() {
        assertThatThrownBy(() -> parseDuration(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("duration cannot be null");
    }

    @Test
    public void invalidDurationFormat() {
      assertThatThrownBy(() -> parseDuration("NOT_A_VALID_INPUT"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Invalid duration pattern : NOT_A_VALID_INPUT");

      assertThatThrownBy(() -> parseDuration("24x"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Invalid duration pattern : 24x");
    }
}
