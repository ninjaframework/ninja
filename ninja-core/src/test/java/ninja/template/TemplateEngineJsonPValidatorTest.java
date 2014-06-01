/**
 * Copyright (C) 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja.template;

import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test for JSONP regular expression callback.
 */
public class TemplateEngineJsonPRegexpTest {

    @Test
    public void testRegularExpression() {
        Pattern re = TemplateEngineJsonP.CALLBACK_VALIDATION_REGEXP;
        assertTrue("simple function", re.matcher("onResponse").matches());
        assertTrue("object function", re.matcher("MyPath.path").matches());
        assertTrue("complex path", re.matcher("Ext.data.JsonP.callback4").matches());

        assertFalse("period in the front, simple", re.matcher(".onResponse").matches());
        assertFalse("period in the end, simple", re.matcher("onResponse.").matches());
        assertFalse("period in the front, object function", re.matcher(".MyPath.path").matches());
        assertFalse("period in the end, complex path", re.matcher("MyPath.path.path2.").matches());
        assertFalse("two subsequent periods", re.matcher("MyPath..path.path2").matches());
    }
}
