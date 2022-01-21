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
 * Represents a subpart of a CRON expression.
 */
public interface CronExpressionPart {

    /**
     * Checks that the value does not match
     *
     * @param value The value to test
     * @return {@code true} if not compliant, otherwise, {@code false}
     */
    boolean isNotCompliant(final int value);

    /**
     * Checks that CRON expression has valid attributes.
     *
     * @param allowedMinStepValue The minimum value for the Step value
     * @param allowedMaxStepValue The maximum value for the Step value
     * @param allowedMinValue     The minimum value
     * @param allowedMaxValue     The maximum value
     */
    void assertViolation(final int allowedMinStepValue,
                         final int allowedMaxStepValue,
                         final int allowedMinValue,
                         final int allowedMaxValue);
}
