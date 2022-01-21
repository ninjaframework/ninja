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

/**
 * CRON expression part representing a range (min, max).
 */
class CronExpressionPartRange extends CronExpressionPartStepValue {

    private final int min;
    private final int max;

    /**
     * Build a new instance.
     *
     * @param stepValue The step value
     * @param min       The min range value
     * @param max       The max range value
     */
    public CronExpressionPartRange(final int stepValue, final int min, final int max) {
        super(stepValue);

        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isNotCompliant(final int value) {
        return super.isNotCompliant(value) ^ (value >= min && value <= max);
    }

    @Override
    public void assertViolation(final int allowedMinStepValue,
                                final int allowedMaxStepValue,
                                final int allowedMinValue,
                                final int allowedMaxValue) {
        super.assertViolation(allowedMinStepValue, allowedMaxStepValue, allowedMinValue, allowedMaxValue);

        if ((min < allowedMinValue) || (max > allowedMaxValue)) {
            throw new BadCronExpressionException(
                    "Range value '%s..%s' is invalid. Allowed Range is '%s..%s'.",
                    min,
                    max,
                    allowedMinValue,
                    allowedMaxValue);
        }
        if (min > max) {
            throw new BadCronExpressionException("Min value '%s' can't be higher than Max value '%s'", min, max);
        }
    }
}
