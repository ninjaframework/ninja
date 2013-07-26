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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.GuestbookEntry;
import ninja.Result;
import ninja.Results;
import ninja.Router;
import ninja.i18n.Lang;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class ApplicationController {

    /**
     * This is the system wide logger. You can still use any config you like. Or
     * create your own custom logger.
     * 
     * But often this is just a simple solution:
     */
    @Inject
    public org.slf4j.Logger logger;
    
    @Inject
    Router router;

    @Inject
    Lang lang;
    
    @Inject 
    Provider<EntityManager> entitiyManagerProvider;
    
    @Transactional
    public Result getIndex() {
                
        EntityManager entityManager = entitiyManagerProvider.get();
            
        Query q = entityManager.createQuery("SELECT x FROM GuestbookEntry x");
        List<GuestbookEntry> guestbookEntries = (List<GuestbookEntry>) q.getResultList();
        
        String postRoute = router.getReverseRoute(ApplicationController.class, "postIndex");
        
        return Results
                .html()
                .render("guestbookEntries", guestbookEntries).
                render("postRoute", postRoute);

        
    }
    
    
    @Transactional
    public Result postIndex(GuestbookEntry guestbookEntry) {
        
        logger.info("In postRoute");        
        
        EntityManager entityManager = entitiyManagerProvider.get();
        
        entityManager.persist(guestbookEntry);

        
        return Results.redirect(router.getReverseRoute(ApplicationController.class, "getIndex"));

    }


}
