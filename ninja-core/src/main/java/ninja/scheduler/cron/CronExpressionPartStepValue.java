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
 * CRON expression part representing a step value.
 */
class CronExpressionPartStepValue implements CronExpressionPart {

    private final int stepValue;

    /**
     * Build a new instance.
     *
     * @param stepValue The value
     */
    protected CronExpressionPartStepValue(final int stepValue) {
        this.stepValue = stepValue;
    }

    @Override
    public boolean isNotCompliant(final int value) {
        return stepValue == -1 || value % stepValue == 0;
    }

    @Override
    public void assertViolation(final int allowedMinStepValue,
                                final int allowedMaxStepValue,
                                final int allowedMinValue,
                                final int allowedMaxValue) {
        if (stepValue != -1) {
            if (!(stepValue >= allowedMinStepValue && stepValue <= allowedMaxStepValue)) {
                throw new BadCronExpressionException(
                        "Step value '%s' is invalid. It must be between '%s..%s'.",
                        stepValue,
                        allowedMaxStepValue,
                        allowedMinStepValue);
            }
        }
    }
}
