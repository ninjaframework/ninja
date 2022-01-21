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
 * Exception thrown if there is a problem when parsing a CRON expression.
 */
public class BadCronExpressionException extends RuntimeException {

    /**
     * Build a new instance.
     *
     * @param errorMessage     The error message format
     * @param stringFormatArgs The arguments for the error message format
     */
    protected BadCronExpressionException(final String errorMessage, final Object... stringFormatArgs) {
        super(String.format(errorMessage, stringFormatArgs));
    }

    /**
     * Build a new instance.
     *
     * @param causeException   The root exception
     * @param errorMessage     The error message
     * @param stringFormatArgs The arguments for the error message format
     */
    protected BadCronExpressionException(final BadCronExpressionException causeException,
                                         final String errorMessage,
                                         final Object... stringFormatArgs) {
        super(String.format(errorMessage, stringFormatArgs), causeException);
    }
}
