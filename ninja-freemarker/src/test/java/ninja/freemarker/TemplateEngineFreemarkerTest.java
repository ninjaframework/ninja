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
package ninja.freemarker;

import javax.inject.Singleton;

import ninja.freemarker.TemplateEngineFreemarker;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class TemplateEngineFreemarkerTest {

    @Test
    public void testThatTemplateEngineFreemarkerHasSingletonAnnotation() {

        Singleton singleton = TemplateEngineFreemarker.class.getAnnotation(Singleton.class);

        Assert.assertThat(singleton, CoreMatchers.notNullValue());

    }

}
