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
package ninja.scheduler.cron;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;

/**
 * Unit tests for class {@link CronExpression}.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CronExpressionTest {

    @Test
    public void everySeconds() {
        final CronExpression cronExpression = new CronExpression("* * * * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 23, 12, 0, 0); // Wednesday, February 23, 2022

        final long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assert.assertEquals(LocalDateTime.of(2022, 1, 23, 12, 0, 1), nextTriggerLocalDateTime);
    }

    @Test
    public void everyMinutes() {
        final CronExpression cronExpression = new CronExpression("0 * * * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 23, 12, 0, 0); // Wednesday, February 23, 2022

        final long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assert.assertEquals(LocalDateTime.of(2022, 1, 23, 12, 1, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyHours() {
        final CronExpression cronExpression = new CronExpression("0 0 * * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 23, 12, 0, 0); // Wednesday, February 23, 2022

        final long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assert.assertEquals(LocalDateTime.of(2022, 1, 23, 13, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyDays() {
        final CronExpression cronExpression = new CronExpression("0 0 0 * * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 23, 12, 0, 0); // Wednesday, February 23, 2022

        final long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assert.assertEquals(LocalDateTime.of(2022, 1, 24, 0, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyMonths() {
        final CronExpression cronExpression = new CronExpression("0 0 0 1 * *");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 2, 23, 12, 0, 0); // Wednesday, February 23, 2022

        final long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assert.assertEquals(LocalDateTime.of(2022, 3, 1, 0, 0, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyTwiceADayFromMondayToFriday() {
        // Twice a day (2h30 & 14h30) from Monday to Friday
        final CronExpression cronExpression = new CronExpression("0 30 2,14 * * 1-5");

        // Given: Wednesday, February 23, 2022 12:00:00
        // Expected: Wednesday, February 23, 2022 14:30:00
        LocalDateTime localDateTime = LocalDateTime.of(2022, 2, 23, 12, 0, 0);
        long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 2, 23, 14, 30, 0), nextTriggerLocalDateTime);

        // Given: Friday, February 25, 2022 22:00:00
        // Expected: Monday, February 28, 2022 02:30:00
        localDateTime = LocalDateTime.of(2022, 2, 25, 22, 0, 0);
        delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 2, 28, 2, 30, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void everyMondayAt22h28_30sec() {
        final CronExpression cronExpression = new CronExpression("30 28 22 * * 1");
        final LocalDateTime localDateTime = LocalDateTime.of(2022, 2, 23, 12, 0, 0); // Wednesday, February 23, 2022

        final long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        final LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);

        Assert.assertEquals(LocalDateTime.of(2022, 2, 28, 22, 28, 30), nextTriggerLocalDateTime);
    }

    @Test
    public void every5Seconds() {
        final CronExpression cronExpression = new CronExpression("*/5 * * * * *");

        // Given: Friday, February 25, 2022 12:00:05
        // Expected: Monday, February 25, 2022 12:00:10
        LocalDateTime localDateTime = LocalDateTime.of(2022, 2, 25, 12, 0, 5); // Wednesday, February 23, 2022
        long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 2, 25, 12, 0, 10), nextTriggerLocalDateTime);

        // Given: Friday, February 25, 2022 12:00:11
        // Expected: Monday, February 25, 2022 12:00:15
        localDateTime = LocalDateTime.of(2022, 2, 25, 12, 0, 11); // Wednesday, February 23, 2022
        delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 2, 25, 12, 0, 15), nextTriggerLocalDateTime);
    }

    @Test
    public void everyOddDayOfTheMonthAt23h59() {
        final CronExpression cronExpression = new CronExpression("0 59 23 */2 * *");

        // Given: Friday, February 25, 2022 12:00:00
        // Expected: Monday, February 26, 2022 23:59:00
        LocalDateTime localDateTime = LocalDateTime.of(2022, 2, 25, 12, 0, 0); // Wednesday, February 23, 2022
        long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 2, 26, 23, 59, 0), nextTriggerLocalDateTime);

        // Given: Friday, February 26, 2022 23:59:00
        // Expected: Monday, February 28, 2022 23:59:00
        localDateTime = LocalDateTime.of(2022, 2, 26, 23, 59, 0); // Wednesday, February 23, 2022
        delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 2, 28, 23, 59, 0), nextTriggerLocalDateTime);
    }

    @Test
    public void from2to5OfEachMonthAt10h12() {
        final CronExpression cronExpression = new CronExpression("0 12 10 2-5 * *");

        // Given: Friday, February 25, 2022 12:00:00
        // Expected: Wednesday, March 02, 2022 10:12:00
        LocalDateTime localDateTime = LocalDateTime.of(2022, 2, 25, 12, 0, 0); // Wednesday, February 23, 2022
        long delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        LocalDateTime nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 3, 2, 10, 12, 0), nextTriggerLocalDateTime);

        // Given: Wednesday, March 02, 2022 10:12:00
        // Expected: Thursday, March 03, 2022 10:12:00
        localDateTime = LocalDateTime.of(2022, 3, 2, 10, 12, 0); // Wednesday, February 23, 2022
        delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 3, 3, 10, 12, 0), nextTriggerLocalDateTime);

        // Given: Friday, March 04, 2022 10:12:00
        // Expected: Saturday, March 05, 2022 10:12:00
        localDateTime = LocalDateTime.of(2022, 3, 4, 10, 12, 0); // Wednesday, February 23, 2022
        delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 3, 5, 10, 12, 0), nextTriggerLocalDateTime);

        // Given: Saturday, March 05, 2022 10:12:00
        // Expected: Saturday, April 02, 2022 10:12:00
        localDateTime = LocalDateTime.of(2022, 3, 5, 10, 12, 0); // Wednesday, February 23, 2022
        delay = cronExpression.getNextDelayMilliseconds(localDateTime);
        nextTriggerLocalDateTime = localDateTime.plusSeconds(delay / 1000);
        Assert.assertEquals(LocalDateTime.of(2022, 4, 2, 10, 12, 0), nextTriggerLocalDateTime);
    }

    @Test(expected = BadCronExpressionException.class)
    public void badPartSingleValueTooHigh() {
        new CronExpression("125 12 10 2-5 * *");
    }

    @Test(expected = BadCronExpressionException.class)
    public void badPartListValueTooHigh() {
        new CronExpression("1,2,71,4 12 10 2-5 * *");
    }

    @Test(expected = BadCronExpressionException.class)
    public void badCronExpressionSize() {
        new CronExpression("1,2,71,4 * *");
    }

    @Test(expected = BadCronExpressionException.class)
    public void badCronExpressionTooManyWildcard() {
        new CronExpression("0 12 10 2-5 ** *");
    }
}
