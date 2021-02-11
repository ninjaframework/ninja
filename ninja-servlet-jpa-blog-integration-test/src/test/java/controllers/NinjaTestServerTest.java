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

package controllers;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import dao.ArticleDao;
import java.util.Map;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaTestServer;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

public class NinjaTestServerTest {
    
    @Test
    public void testThatOverrideOfPropertiesWorks() {
        // given
        Map<String, String> overrideProperties = ImmutableMap.of(
                "property.that.should.be.overwritten.programmatically", "hooray!");
        
        // when
        NinjaTestServer ninjaTestServer = NinjaTestServer.builder()
                .overrideProperties(overrideProperties)
                .build();
        
        // then
        NinjaProperties ninjaProperties = ninjaTestServer.getInjector().getInstance(NinjaProperties.class);
        String actualValue = ninjaProperties.get("property.that.should.be.overwritten.programmatically");
        
        Assertions.assertThat(actualValue).isEqualTo("hooray!");
        
        ninjaTestServer.shutdown();
    }
    
    @Test
    public void testThatOverrideModule_works() {
        // given
        ArticleDao mockedArticleDao = Mockito.mock(ArticleDao.class);
        AbstractModule overrideModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ArticleDao.class).toInstance(mockedArticleDao);
            } 
        };

        // when
        NinjaTestServer ninjaTestServer = NinjaTestServer.builder()
                .overrideModule(overrideModule)
                .build();
        
        // then
        ArticleDao actual = ninjaTestServer.getInjector().getInstance(ArticleDao.class);
        Assertions.assertThat(actual).isEqualTo(mockedArticleDao);
        
        ninjaTestServer.shutdown();
    }

}
