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

package ninja.jpa;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnitOfWorkInterceptorTest {
    

    @Mock
    MethodInvocation methodInvocation;
    
    @Mock
    com.google.inject.persist.UnitOfWork unitOfWork;
    
    
    @Test
    public void assertThatSimultaneouslyUsingAnnotationOnManyLevelsWorks() throws Throwable {
        
        // only the most outer annotation should open and close stuff...

        UnitOfWorkInterceptor unitOfWorkInterceptor = new UnitOfWorkInterceptor();
        unitOfWorkInterceptor.unitOfWork = unitOfWork;
        // already started...
        unitOfWorkInterceptor.didWeStartWork.set(Boolean.TRUE);
        
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

        UnitOfWorkInterceptor unitOfWorkInterceptor = new UnitOfWorkInterceptor();
        unitOfWorkInterceptor.unitOfWork = unitOfWork;
        
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
