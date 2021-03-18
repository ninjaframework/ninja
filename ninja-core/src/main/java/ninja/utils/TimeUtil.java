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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

@ParametersAreNonnullByDefault
public class TimeUtil {

    private static final Pattern REGEX = Pattern.compile("^([0-9]+)(d|h|min|mn|s)$");

    /**
     * Parse a duration from String to seconds. 
     * Eg. "10s" will result in 10.
     * 
     * @param duration "3h" or "2mn" or "2min" or "7s" or "1d".
     * 
     * @return The number of seconds OR 30days (2592000) if null is entered.
     *
     * @throws IllegalArgumentException if parameter is null or has other format
     */
    public static int parseDuration(String duration) {
        if (duration == null) {
            throw new IllegalArgumentException("duration cannot be null");
        }

        Matcher m = REGEX.matcher(duration);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid duration pattern : " + duration);
        }
        int value = parseInt(m.group(1));
        String units = m.group(2);
        switch (units) {
            case "d":
                return value * 60 * 60 * 24;
            case "h":
                return value * 60 * 60;
            case "min":
            case "mn":
                return value * 60;
            case "s":
                return value;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + units);
        }
    }
}
