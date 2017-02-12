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
package ninja.params;

import ninja.validation.Validation;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ParamParsersTest {
    
    @Mock
    Validation validation;
    
    @Test
    public void testBooleanParamParser() {
           ParamParsers.BooleanParamParser booleanParamParser = new ParamParsers.BooleanParamParser();
           
           assertThat(booleanParamParser.getParsedType(), Matchers.is(Boolean.class));
           
           assertThat(booleanParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
           assertThat(booleanParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());
           assertThat(booleanParamParser.parseParameter("param1", "123123", validation), Matchers.nullValue());
           assertThat(booleanParamParser.parseParameter("param1", "-", validation), Matchers.nullValue());
           assertThat(booleanParamParser.parseParameter("param1", "+", validation), Matchers.nullValue());
           
           assertThat(booleanParamParser.parseParameter("param1", "true", validation), Matchers.is(Boolean.TRUE));
           assertThat(booleanParamParser.parseParameter("param1", "TRUE", validation), Matchers.is(Boolean.TRUE));
           
           assertThat(booleanParamParser.parseParameter("param1", "false", validation), Matchers.is(Boolean.FALSE));
           assertThat(booleanParamParser.parseParameter("param1", "FALSE", validation), Matchers.is(Boolean.FALSE));
    }
    
}
