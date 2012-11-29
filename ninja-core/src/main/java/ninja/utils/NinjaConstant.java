/**
 * Copyright (C) 2012 the original author or authors.
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

package ninja.utils;

public interface NinjaConstant {

    // /////////////////////////////////////////////////
    // The 3 basic modes for ninja.
    // they should be set as system property: -Dninja.mode=test
    // and so on
    String MODE_KEY_NAME = "ninja.mode";
    // and the values for the modes:
    String MODE_TEST = "test";
    String MODE_DEV = "dev";
    String MODE_PROD = "prod";

    // location of the default views for errors:
    String LOCATION_VIEW_FTL_HTML_NOT_FOUND = "views/system/404notFound.ftl.html";
    String LOCATION_VIEW_FTL_HTML_FORBIDDEN = "views/system/403forbiddden.ftl.html";

    /**
     * Comma separated list in application.conf. Determines which languages are
     * loaded for the application.
     * 
     * Something like
     * 
     * ninja.application.languages=de,en
     */
    final String applicationLanguages = "application.languages";

    /**
     * Prefix used for all Ninja cookies.
     * 
     * Make sure you set the prefix in your application.conf file.
     * 
     * */
    final String applicationCookiePrefix = "application.cookie.prefix";

    /** Used to verify client side cookie for instance. */
    final String applicationName = "application.name";

    /** Used to verify client side cookie for instance. */
    final String applicationSecret = "application.secret";

    /**
     * In many situations the server cannot know its own name. So You can set
     * using that variable.
     */
    final String serverName = "application.server.name";

    /**
     * 
     */
    final String sessionExpireTimeInSeconds = "application.session.expire_time_in_seconds";

    /**
     * 
     */
    final String sessionSendOnlyIfChanged = "application.session.send_only_if_changed";

    /**
     * Used to set the Secure flag if the cookie. Means Session will only be
     * transferrd over Https.
     */
    final String sessionTransferredOverHttpsOnly = "application.session.transferred_over_https_only";

    /**
     * Suffix used for Ninja cookies. Usually results in cookies like
     * "NINJA_SESSION
     */
    final String SESSION_SUFFIX = "_SESSION";

    /**
     * Suffix used for Ninja cookies. Usually results in cookies like
     * "NINJA_FLASH
     */
    final String FLASH_SUFFIX = "_FLASH";

    /** Used as spacer for instance in session cookie */
    final String UNI_CODE_NULL_ENTITY = "\u0000";

    /** yea. utf-8 */
    final String UTF_8 = "utf-8";

}
