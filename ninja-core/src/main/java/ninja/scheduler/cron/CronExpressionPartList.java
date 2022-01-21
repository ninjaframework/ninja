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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * CRON expression part representing a list of integers.
 */
class CronExpressionPartList extends CronExpressionPartStepValue {

    private final List<Integer> lst;

    /**
     * Build a new instance.
     *
     * @param stepValue The step value
     * @param lst       A list of integers
     */
    public CronExpressionPartList(final int stepValue, final List<Integer> lst) {
        super(stepValue);

        this.lst = lst != null ? lst : Collections.emptyList();
    }

    @Override
    public boolean isNotCompliant(final int value) {
        return super.isNotCompliant(value) ^ lst.contains(value);
    }

    @Override
    public void assertViolation(final int allowedMinStepValue,
                                final int allowedMaxStepValue,
                                final int allowedMinValue,
                                final int allowedMaxValue) {
        super.assertViolation(allowedMinStepValue, allowedMaxStepValue, allowedMinValue, allowedMaxValue);

        if (lst.stream().anyMatch(value -> (value < allowedMinValue) || (value > allowedMaxValue))) {
            throw new BadCronExpressionException(
                    "List '%s' is invalid. All values must be between '%s..%s'.",
                    Arrays.toString(lst.toArray()),
                    allowedMinValue,
                    allowedMaxValue);
        }
    }
}
