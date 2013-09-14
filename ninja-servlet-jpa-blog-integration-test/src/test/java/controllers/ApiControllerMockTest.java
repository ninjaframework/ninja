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

package controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import ninja.Result;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import dao.ArticleDao;

@RunWith(MockitoJUnitRunner.class)
public class ApiControllerMockTest {

    @Mock
    ArticleDao articleDao;
    
    ApiController apiController;
    
    @Before
    public void setupTest() {
        apiController = new ApiController();
        apiController.articleDao = articleDao;
        
    }
    

    @Test
    public void testThatPostArticleReturnsOkWhenArticleDaoReturnsTrue() {

        when(articleDao.postArticle(null, null)).thenReturn(true);
        
        Result result = apiController.postArticleJson(null, null);
        
        assertEquals(200, result.getStatusCode());

    }
    
    @Test
    public void testThatPostArticleReturnsNotFoundWhenArticleDaoReturnsFalse() {

        when(articleDao.postArticle(null, null)).thenReturn(false);
        
        Result result = apiController.postArticleJson(null, null);
        
        assertEquals(404, result.getStatusCode());

    }

}
