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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class TimeUtilTest {

    @Test
    public void durationInDays() {
        assertEquals(86400, TimeUtil.parseDuration("1d"));
        assertEquals(2592000, TimeUtil.parseDuration("30d"));
    }

    @Test
    public void durationInSeconds() {
        assertEquals(10, TimeUtil.parseDuration("10s"));
    }

    @Test
    public void durationCannotBeNull() {
        assertThatThrownBy(() -> TimeUtil.parseDuration(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("duration cannot be null");
    }

    @Test
    public void invalidDurationFormat() {
      assertThatThrownBy(() -> TimeUtil.parseDuration("NOT_A_VALID_INPUT"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Invalid duration pattern : NOT_A_VALID_INPUT");
    }
}
