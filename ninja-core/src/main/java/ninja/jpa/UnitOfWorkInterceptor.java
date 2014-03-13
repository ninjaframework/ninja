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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import javax.persistence.EntityManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * See @UnitOfWork.
 * 
 * This interceptor tracks and opens and closes your database connections.
 * 
 * @author Raphael A. Bauer
 */
public class UnitOfWorkInterceptor implements MethodInterceptor {

    final Provider<com.google.inject.persist.UnitOfWork> unitOfWorkProvider;
    
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
    
    final Provider<EntityManager> entityManagerProvider;

    @Inject
    public UnitOfWorkInterceptor(
            Provider<com.google.inject.persist.UnitOfWork> unitOfWorkProvider,
            Provider<EntityManager> entityManagerProvider) {
        this.unitOfWorkProvider = unitOfWorkProvider;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        
        final UnitOfWork unitOfWork;
        
        // Only start a new unit of work if the entitymanager is empty
        // otherwise someone else has started the unit of work already
        // and we do nothing
        // Please compare to com.google.inject.persist.jpa.JpaLocalTxnInterceptor
        // we just mimick that interceptor
        //
        // IMPORTANT:
        // Nesting of begin() end() of unitOfWork is NOT allowed. Contrary to
        // the documentation of Google Guice as of March 2014.
        // Related Ninja issue: https://github.com/ninjaframework/ninja/issues/157
        if (entityManagerProvider.get() == null) {
        
            unitOfWork = unitOfWorkProvider.get();
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
