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

package ninja.jpa;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.persist.PersistService;

@RunWith(MockitoJUnitRunner.class)
public class JpaInitializerTest {

    @Mock
    PersistService persistService;
    
    @Test
    public void testStart() throws Exception {
        
        JpaInitializer jpaInitializer = new JpaInitializer(persistService);
        jpaInitializer.start();
        
        verify(persistService).start();
        
        // we also verify that the annotation is ok
        Method method = JpaInitializer.class.getMethod("start");
        Start start = method.getAnnotation(Start.class);
    
        assertEquals(10, start.order());
    }
    
    @Test
    public void testStop() throws Exception {
        
        JpaInitializer jpaInitializer = new JpaInitializer(persistService);
        jpaInitializer.stop();
        
        verify(persistService).stop();
        
        // we also verify that the annotation is ok
        Method method = JpaInitializer.class.getMethod("stop");
        Dispose dispose = method.getAnnotation(Dispose.class);
    
        assertEquals(10, dispose.order());
    }

}
