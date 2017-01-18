/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtil {
    
    /** From here: http://www.ietf.org/rfc/rfc1123.txt */
    private static final DateTimeFormatter RFC1123_DATE_FORMAT = DateTimeFormat
            .forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'")
            .withLocale(Locale.US)
            .withZone(DateTimeZone.UTC);
    
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
        return RFC1123_DATE_FORMAT.print(new DateTime(date));
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
        
        return RFC1123_DATE_FORMAT.print(new DateTime(unixTime));
    }
    
    
    /**
     * Can be used to parse http times. For instance something like a http header
     * Date: Tue, 26 Mar 2013 13:47:13 GMT
     * 
     * INFO: consider the JodaTime based DateUtil.parseHttpDateFormatToDateTime(...) version
     * 
     * @param httpDateFormat in http format: Date: Tue, 26 Mar 2013 13:47:13 GMT
     * @return A nice "Date" object containing that http timestamp.
     * @throws ParseException If something goes wrong.
     */
    public static Date parseHttpDateFormat(String httpDateFormat) throws IllegalArgumentException {
               
        return parseHttpDateFormatToDateTime(httpDateFormat).toDate();

    }
    
    /**
     * Can be used to parse http times. For instance something like a http header
     * Date: Tue, 26 Mar 2013 13:47:13 GMT
     * 
     * @param httpDateFormat in http format: Date: Tue, 26 Mar 2013 13:47:13 GMT
     * @return A nice "DateTime" (JodaTime) object containing that http timestamp.
     * @throws ParseException If something goes wrong.
     */
    public static DateTime parseHttpDateFormatToDateTime(String httpDateFormat) throws IllegalArgumentException {
               
        return RFC1123_DATE_FORMAT.parseDateTime(httpDateFormat);

    }

}
