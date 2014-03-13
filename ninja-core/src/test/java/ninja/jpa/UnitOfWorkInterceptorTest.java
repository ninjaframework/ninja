/**
 * Copyright (C) 2012-2014 the original author or authors.
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

import javax.persistence.EntityManager;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnitOfWorkInterceptorTest {
    
    @Mock
    MethodInvocation methodInvocation;
    
    @Mock
    com.google.inject.persist.UnitOfWork unitOfWork;
    
    @Mock
    com.google.inject.Provider<com.google.inject.persist.UnitOfWork> unitOfWorkProvider;
    
    @Mock
    EntityManager entityManager;
        
    @Mock
    com.google.inject.Provider<EntityManager> entityManagerProvider;
    
    @Before
    public void before() {
    
        Mockito.when(unitOfWorkProvider.get()).thenReturn(unitOfWork);
        Mockito.when(entityManagerProvider.get()).thenReturn(entityManager);
       
    }
    
    @Test
    public void assertThatSimultaneouslyUsingAnnotationOnManyLevelsWorks() throws Throwable {
        
        // only the most outer annotation should open and close stuff...

        UnitOfWorkInterceptor unitOfWorkInterceptor 
                = new UnitOfWorkInterceptor(unitOfWorkProvider, entityManagerProvider);
        
        // EntityManager already started (not null) therefore we should NOT
        // start a unitOfWork
        Mockito.when(entityManagerProvider.get()).thenReturn(entityManager);
        
        // execute method invocation
        unitOfWorkInterceptor.invoke(methodInvocation);
        
        // no unitOfWork begin
        Mockito.verify(unitOfWork, Mockito.never()).begin();
        // no unitOfWork ended
        Mockito.verify(unitOfWork, Mockito.never()).end();
        
        // but method has been invoked
        Mockito.verify(methodInvocation).proceed();
        
    }
    
    
    @Test
    public void assertAnnotationStartsAndEndsNewUnitOfWork() throws Throwable {
        
        // only the most outer annotation should open and close stuff...

        UnitOfWorkInterceptor unitOfWorkInterceptor 
                = new UnitOfWorkInterceptor(unitOfWorkProvider, entityManagerProvider);
        
        // not started => unitOfWork should be started
        Mockito.when(entityManagerProvider.get()).thenReturn(null);
        
        // execute method invocation
        unitOfWorkInterceptor.invoke(methodInvocation);
        
        // no unitOfWork begin
        Mockito.verify(unitOfWork).begin();
        // no unitOfWork ended
        Mockito.verify(unitOfWork).end();
        
        // but method has been invoked
        Mockito.verify(methodInvocation).proceed();
        
    }
    
}
