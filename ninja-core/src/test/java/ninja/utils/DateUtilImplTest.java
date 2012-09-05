package ninja.utils;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class DateUtilImplTest {

    @Test
    public void testFormatForHttpHeaderDate() {
        //some simple tests:
        assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", DateUtil.formatForHttpHeader(new Date(0L)));
        assertEquals("Wed, 05 Sep 2012 09:57:57 GMT", DateUtil.formatForHttpHeader(new Date(1346839077523L)));
    }

    @Test
    public void testFormatForHttpHeaderLong() {
        //some simple tests:
        assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", DateUtil.formatForHttpHeader(0L));
        assertEquals("Wed, 05 Sep 2012 09:57:57 GMT", DateUtil.formatForHttpHeader(1346839077523L));

    }
}
