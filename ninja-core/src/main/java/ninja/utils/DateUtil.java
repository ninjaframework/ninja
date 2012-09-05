/**
 * Copyright (C) 2012 the original author or authors.
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    
    private static SimpleDateFormat httpHeaderDateFormat;
    
    static {
        httpHeaderDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        httpHeaderDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));        
    }
    
    /**
     * Can be used to format a date into http header compatible 
     * strings.
     * 
     * It can be used to generate something like:
     * Date: Wed, 05 Sep 2012 09:16:19 GMT
     * Expires: Thu, 01 Jan 1970 00:00:00 GMT
     * 
     * @param date The date to format
     * @return a http header compatible string like "Thu, 01 Jan 1970 00:00:00 GMT"
     */
    public static String formatForHttpHeader(Date date) {
        return httpHeaderDateFormat.format(date);
    }
    
    /**
     * Can be used to format a unix timestamp into 
     * http header compatible strings.
     * 
     * It can be used to generate something like:
     * Date: Wed, 05 Sep 2012 09:16:19 GMT
     * Expires: Thu, 01 Jan 1970 00:00:00 GMT
     * 
     * @param date The long (unixtime) to format
     * @return a http header compatible string like "Thu, 01 Jan 1970 00:00:00 GMT"
     */
    public static String formatForHttpHeader(Long unixTime) {
        return httpHeaderDateFormat.format(new Date(unixTime));
    }

}
