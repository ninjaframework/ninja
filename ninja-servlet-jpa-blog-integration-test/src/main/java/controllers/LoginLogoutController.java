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

package controllers;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import dao.UserDao;

@Singleton
public class LoginLogoutController {
    
    @Inject
    UserDao userDao;
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Login
    ///////////////////////////////////////////////////////////////////////////
    public Result login(Context context) {

        return Results.html();

    }

    public Result loginPost(@Param("username") String username,
                            @Param("password") String password,
                            Context context) {

        boolean isUserNameAndPasswordValid = userDao.isUserAndPasswordValid(username, password);
        
        
        if (isUserNameAndPasswordValid) {
            context.getSessionCookie().put("username", username);
            context.getFlashCookie().success("login.loginSuccessful");
            
            return Results.redirect("/");
            
        } else {
            
            // something is wrong with the input or password not found.
            context.getFlashCookie().put("username", username);
            context.getFlashCookie().error("login.errorLogin");

            return Results.redirect("/login");
            
        }
        
    }

    ///////////////////////////////////////////////////////////////////////////
    // Logout
    ///////////////////////////////////////////////////////////////////////////
    public Result logout(Context context) {

        // remove any user dependent information
        context.getSessionCookie().clear();
        context.getFlashCookie().success("login.logoutSuccessful");

        return Results.redirect("/");

    }

}
