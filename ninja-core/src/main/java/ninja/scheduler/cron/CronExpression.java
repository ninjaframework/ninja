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

import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represent a CRON expression.
 */
public class CronExpression {

    private static final Pattern REGEXP_PATTERN_RANGE = Pattern.compile("^([^-]+)-([^-/]+)(/(\\d+))?$");
    private static final Pattern REGEXP_PATTERN_LIST = Pattern.compile("^([^-/]+)(/(\\d+))?$");
    private static final Pattern REGEXP_PATTERN_SINGLE = Pattern.compile("^(\\d+)(/(\\d+))?$");

    private static final List<String> WILDCARD_REPLACEMENT = Arrays.asList(
            "0-59",  // Second
            "0-59",  // Minute
            "0-23",  // Hour
            "1-31",  // Day of Month
            "1-12",  // Month
            "0-6");  // Day of Week

    private static final List<Consumer<CronExpressionPart>> CRON_EXPRESSION_PART_VALIDATOR = Arrays.asList(
            (c) -> c.assertViolation(1, 60, 0, 59),  // Second
            (c) -> c.assertViolation(1, 60, 0, 59),  // Minute
            (c) -> c.assertViolation(1, 24, 0, 23),  // Hour
            (c) -> c.assertViolation(1, 32, 0, 31),  // Day of Month
            (c) -> c.assertViolation(1, 13, 0, 12),  // Month
            (c) -> c.assertViolation(1, 7, 0, 6));   // Day of Week

    private static final int IDX_SECOND = 0;
    private static final int IDX_MINUTE = 1;
    private static final int IDX_HOUR = 2;
    private static final int IDX_DAY_OF_MONTH = 3;
    private static final int IDX_MONTH = 4;
    private static final int IDX_DAY_OF_WEEK = 5;

    final Map<DayOfWeek, Integer> DAY_OF_WEEK_CRON_VALUE = new HashMap<DayOfWeek, Integer>() {{
        put(DayOfWeek.SUNDAY, 0);
        put(DayOfWeek.MONDAY, 1);
        put(DayOfWeek.TUESDAY, 2);
        put(DayOfWeek.WEDNESDAY, 3);
        put(DayOfWeek.THURSDAY, 4);
        put(DayOfWeek.FRIDAY, 5);
        put(DayOfWeek.SATURDAY, 6);
    }};

    private final CronExpressionPart[] cronExpressionPartArray;

    /**
     * Build a new instance.
     *
     * @param cron The CRON expression to parse
     */
    public CronExpression(final String cron) {
        // ┌───────────── second (0 - 59)
        // │ ┌───────────── minute (0 - 59)
        // │ │ ┌───────────── hour (0 - 23)
        // │ │ │ ┌───────────── day of the month (1 - 31)
        // │ │ │ │ ┌───────────── month (1 - 12)
        // │ │ │ │ │ ┌───────────── day of the week (0 - 6) (Sunday to Saturday)
        // │ │ │ │ │ │
        // │ │ │ │ │ │
        // * * * * * *


        final String[] cronArray = cron.split(StringUtils.SPACE);
        if (cronArray.length < 5) {
            throw new BadCronExpressionException("CRON expression is invalid '%s'", cron);
        }

        cronExpressionPartArray = new CronExpressionPart[6];
        for (int idx = 0; idx < cronExpressionPartArray.length; ++idx) {
            // Standardize CRON expression
            final String standardizedPart = cronArray[idx]
                    .replace("?", "*")
                    .replace("*", WILDCARD_REPLACEMENT.get(idx));

            // Parse and validate each part
            try {
                cronExpressionPartArray[idx] = parseCronExpressionPart(standardizedPart);
                if (idx < CRON_EXPRESSION_PART_VALIDATOR.size()) {
                    CRON_EXPRESSION_PART_VALIDATOR.get(idx).accept(cronExpressionPartArray[idx]);
                }
            } catch (final BadCronExpressionException ex) {
                throw new BadCronExpressionException(
                        ex,
                        "Can't use CRON '%s', error with the part #%d '%s'",
                        cron,
                        idx + 1,
                        cronArray[idx]);
            }
        }
    }

    /**
     * Retrieves the delay from now to the next match.
     *
     * @param zoneId The Zone to use for manipulating datetime
     * @return The next delay in milliseconds
     */
    public long getNextDelayMilliseconds(final ZoneId zoneId) {
        return getNextDelayMilliseconds(LocalDateTime.now(zoneId));
    }

    /**
     * Retrieves the delay from given datetime to the next match.
     *
     * @param from The datetime
     * @return The next delay in milliseconds
     */
    protected long getNextDelayMilliseconds(final LocalDateTime from) {
        LocalDateTime nextTrigger = from.withNano(0).plusSeconds(1);

        CronExpressionPart cronExpressionPart = cronExpressionPartArray[IDX_SECOND];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getSecond())) {
            nextTrigger = nextTrigger.plusSeconds(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_MINUTE];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getMinute())) {
            nextTrigger = nextTrigger.plusMinutes(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_HOUR];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getHour())) {
            nextTrigger = nextTrigger.plusHours(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_DAY_OF_MONTH];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getDayOfMonth())) {
            nextTrigger = nextTrigger.plusDays(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_MONTH];
        while (cronExpressionPart.isNotCompliant(nextTrigger.getMonthValue())) {
            nextTrigger = nextTrigger.plusMonths(1);
        }

        cronExpressionPart = cronExpressionPartArray[IDX_DAY_OF_WEEK];
        while (cronExpressionPart.isNotCompliant(DAY_OF_WEEK_CRON_VALUE.get(nextTrigger.getDayOfWeek()))) {
            nextTrigger = nextTrigger.plusDays(1);
        }

        return ChronoUnit.MILLIS.between(from, nextTrigger);
    }

    private CronExpressionPart parseCronExpressionPart(final String str) {
        CronExpressionPart cronExpressionPart = null;

        if (str.contains("-")) {
            final Matcher matcher = REGEXP_PATTERN_RANGE.matcher(str);
            if (matcher.find()) {
                final int minRange = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
                final int maxRange = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : minRange;
                final int stepValue = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : -1;

                cronExpressionPart = new CronExpressionPartRange(stepValue, minRange, maxRange);
            }
        } else if (str.contains(",")) {
            final Matcher matcher = REGEXP_PATTERN_LIST.matcher(str);
            if (matcher.find()) {
                final String listValue = matcher.group(1);
                final int stepValue = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : -1;

                cronExpressionPart = new CronExpressionPartList(
                        stepValue,
                        Arrays.stream(listValue.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
            }
        } else {
            final Matcher matcher = REGEXP_PATTERN_SINGLE.matcher(str);
            if (matcher.find()) {
                final int singleValue = Integer.parseInt(matcher.group(1));
                final int stepValue = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : -1;

                cronExpressionPart = new CronExpressionPartRange(stepValue, singleValue, singleValue);
            }
        }

        if (cronExpressionPart == null) {
            throw new BadCronExpressionException("Can't parse CRON expression part: %s", str);
        }

        return cronExpressionPart;
    }
}
