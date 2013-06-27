/**
 * Copyright (C) 2013 the original author or authors.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

    private static final Pattern days = Pattern.compile("^([0-9]+)d$");
    private static final Pattern hours = Pattern.compile("^([0-9]+)h$");
    private static final Pattern minutes = Pattern.compile("^([0-9]+)mi?n$");
    private static final Pattern seconds = Pattern.compile("^([0-9]+)s$");

    /**
     * Parse a duration from String to seconds. 
     * Eg. "10s" will result in 10.
     * 
     * @param duration "3h" or "2mn" or "7s" or null.
     * 
     * @return The number of seconds OR 30days (2592000) if null is entered.
     */
    public static int parseDuration(String duration) {
        if (duration == null) {
            return 60 * 60 * 24 * 30;
        }
        int toAdd = -1;
        if (days.matcher(duration).matches()) {
            Matcher matcher = days.matcher(duration);
            matcher.matches();
            toAdd = Integer.parseInt(matcher.group(1)) * (60 * 60) * 24;
        } else if (hours.matcher(duration).matches()) {
            Matcher matcher = hours.matcher(duration);
            matcher.matches();
            toAdd = Integer.parseInt(matcher.group(1)) * (60 * 60);
        } else if (minutes.matcher(duration).matches()) {
            Matcher matcher = minutes.matcher(duration);
            matcher.matches();
            toAdd = Integer.parseInt(matcher.group(1)) * (60);
        } else if (seconds.matcher(duration).matches()) {
            Matcher matcher = seconds.matcher(duration);
            matcher.matches();
            toAdd = Integer.parseInt(matcher.group(1));
        }
        if (toAdd == -1) {
            throw new IllegalArgumentException("Invalid duration pattern : " + duration);
        }
        return toAdd;
    }

}
