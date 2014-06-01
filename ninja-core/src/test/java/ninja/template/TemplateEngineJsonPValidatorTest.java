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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test for JSONP regular expression callback.
 */
public class TemplateEngineJsonPValidatorTest {

    @Test
    public void testRegularExpression() {
        assertTrue("simple function", TemplateEngineJsonP.isValidCallback("onResponse"));
        assertTrue("object function", TemplateEngineJsonP.isValidCallback("MyPath.path"));
        assertTrue("object function", TemplateEngineJsonP.isValidCallback("MyApp.Path.myCallback123"));
        assertTrue("object function, path with numbers", 
                TemplateEngineJsonP.isValidCallback("MyApp123.Path789.myCallback123"));
        assertTrue("complex path", TemplateEngineJsonP.isValidCallback("Ext.data.JsonP.callback4"));
        assertTrue("complex path, $ in identity.", TemplateEngineJsonP.isValidCallback("$42.ajaxHandler"));

        assertFalse("wrong first character", TemplateEngineJsonP.isValidCallback("42$.q"));
        assertFalse("period in the front, simple", TemplateEngineJsonP.isValidCallback(".onResponse"));
        assertFalse("period in the end, simple", TemplateEngineJsonP.isValidCallback("onResponse."));
        assertFalse("period in the front, object function", TemplateEngineJsonP.isValidCallback(".MyPath.path"));
        assertFalse("period in the end, complex path", TemplateEngineJsonP.isValidCallback("MyPath.path.path2."));
        assertFalse("two subsequent periods", TemplateEngineJsonP.isValidCallback("MyPath..path.path2"));
        assertFalse("simple array", TemplateEngineJsonP.isValidCallback("alert(document.cookie)"));
        
        // Cases not supported by the validator.
        assertFalse("simple array", TemplateEngineJsonP.isValidCallback("somearray[12345]"));
        assertFalse("simple array", TemplateEngineJsonP.isValidCallback("\\u0062oo"));
        assertFalse("simple array", TemplateEngineJsonP.isValidCallback("\\u0020"));
    }
}
