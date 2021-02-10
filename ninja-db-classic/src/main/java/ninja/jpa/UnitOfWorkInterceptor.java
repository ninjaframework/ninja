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

package ninja.jpa;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;

/**
 * See @UnitOfWork.
 * 
 * This interceptor tracks and opens and closes your database connections.
 * 
 * @author Raphael A. Bauer
 */
public class UnitOfWorkInterceptor implements MethodInterceptor {

    @Inject
    com.google.inject.persist.UnitOfWork unitOfWork;
    
    // ThreadLocal<Boolean> tracks if the unit of work was begun 
    // implicitly by this thread.
    // According to the docs we can start and end the UnitOfWork as often
    // as we want. But this has to be balanced in some way. Otherwise we get:
    //
    // java.lang.IllegalStateException: Work already begun on this thread. Looks like you have called UnitOfWork.begin() twice without a balancing call to end() in between.
    // at com.google.inject.internal.util.$Preconditions.checkState(Preconditions.java:142) ~[guice-3.0.jar:na]
    // at com.google.inject.persist.jpa.JpaPersistService.begin(JpaPersistService.java:66) ~[guice-persist-3.0.jar:na]
    //    
    // That way all begin() and end() calls are balanced
    // because we only have one unit for this thread.
    final ThreadLocal<Boolean> didWeStartWork = new ThreadLocal<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        
        if (null == didWeStartWork.get()) {
            
            unitOfWork.begin();
            didWeStartWork.set(Boolean.TRUE);
            
        } else {
            // If unit of work already started we don't do anything here...
            // another UnitOfWorkInterceptor point point will take care...
            // This happens if you are nesting your calls.
            return invocation.proceed();
        }

        try {

            return invocation.proceed();

        } finally {

            if (null != didWeStartWork.get()) {
                
                didWeStartWork.remove();
                unitOfWork.end();
                
            }

        }

    }

}
