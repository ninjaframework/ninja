/**
 * Copyright (C) 2012-2015 the original author or authors.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ninja.NinjaTest;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

import dao.ArticleDao;

public class ApplicationControllerTest extends NinjaTest {

    @Test
    public void testThatSetupInitializationCreatesTheTestUsers(){
        // Get the application injector
        Injector applicationInjector = getInjector();
        // Access internal dao
        ArticleDao articleDao = applicationInjector.getInstance(ArticleDao.class);
        // check that articles are setup
        assertEquals(articleDao.getAllArticles().articles.size(), 3);
    }

    @Test
    public void testThatHomepageWorks() {

        // /redirect will send a location: redirect in the headers
        String result = ninjaTestBrowser.makeRequest(getServerAddress() + "/");

        // If the redirect has worked we must see the following text
        // from the index screen:
        assertTrue(result.contains("second"));

    }

}
