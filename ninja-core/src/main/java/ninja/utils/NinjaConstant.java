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

    // /////////////////////////////////////////////////
    // The basic directories used in all convention
    // over configuration operations:
    String VIEWS_DIR = "views";
    String CONTROLLERS_DIR = "controllers";
    String MODELS_DIR = "models";

    // location of the default views for errors:
    String LOCATION_VIEW_FTL_HTML_NOT_FOUND = "views/system/404notFound.ftl.html";
    String LOCATION_VIEW_FTL_HTML_FORBIDDEN = "views/system/403forbidden.ftl.html";

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
     * A cookie that helps Ninja to set a default language.
     * Usually resolves to a cookie called NINJA_LANG.
     * The cookie then looks like: "NINJA_LANG=en"
     */
    final String LANG_COOKIE_SUFFIX = "_LANG";

    /**
     * Prefix used for all Ninja cookies.
     * 
     * Make sure you set the prefix in your application.conf file.
     * 
     * */
    final String applicationCookiePrefix = "application.cookie.prefix";

    /**
     * Enables session/cookie sharing between subdomains. For example, to make cookies valid for
     * all domains ending with ‘.example.com’, e.g. foo.example.com and bar.example.com:
     */
    final String applicationCookieDomain = "application.cookie.domain";


    /** Used to verify client side cookie for instance. */
    final String applicationName = "application.name";

    /** Used to verify client side cookie for instance. */
    final String applicationSecret = "application.secret";

    /**
     * In many situations the server cannot know its own name. So you can set the name
     * using that variable.
     */
    final String serverName = "application.server.name";

    /**
     * Time until session expires.
     */
    final String sessionExpireTimeInSeconds = "application.session.expire_time_in_seconds";

    /**
     * Send session cookie only back when content has changed.
     */
    final String sessionSendOnlyIfChanged = "application.session.send_only_if_changed";

    /**
     * Used to set the Secure flag if the cookie. Means Session will only be
     * transferrd over Https.
     */
    final String sessionTransferredOverHttpsOnly = "application.session.transferred_over_https_only";

    /**
     * Used to set the HttpOnly flag at the session cookie. On a supported
     * browser, an HttpOnly session cookie will be used only when transmitting
     * HTTP (or HTTPS) requests, thus restricting access from other, non-HTTP
     * APIs (such as JavaScript). This restriction mitigates but does not
     * eliminate the threat of session cookie theft via cross-site scripting
     * (XSS).
     */
    final String sessionHttpOnly = "application.session.http_only";
    
    ///////////////////////////////////////////////////////////////////////////
    // Cache configuration
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Constant used to bind custom cache implementation.
     */
    public final String CACHE_IMPLEMENTATION = "cache.implementation";
    
    
    /**
     * Either a single host or a list of hosts (space separated)
     * 
     */
    public final String MEMCACHED_HOST = "memcached.host";
    
    /**
     * Username for memcached (OPTIONAL)
     */
    public final String MEMCACHED_USER =  "memcached.user";
    
    /**
     * Password for memcached (OPTIONAL)
     */
    public final String MEMCACHED_PASSWORD =  "memcached.password";

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
    
    /** Value to set max age in header. E.g. Cache-Control:max-age=XXXXXX */
    final String HTTP_CACHE_CONTROL = "http.cache_control";
    
    /** Default value for Cache-Control http header when not set in application.conf */
    final String HTTP_CACHE_CONTROL_DEFAULT = "3600";
    
    /** Enable / disable etag E.g. ETag:"f0680fd3" */
    final String HTTP_USE_ETAG = "http.useETag";
    
    /** Default value / etag enabled by default. */
    final boolean HTTP_USE_ETAG_DEFAULT = true;
    
    /**
     * Database stuff and JPA
     */    
    /** run migrations on startup of application */
    final String NINJA_MIGRATION_RUN = "ninja.migration.run";
    
    /** The name of the persistence unit to use */
    String PERSISTENCE_UNIT_NAME = "ninja.jpa.persistence_unit_name";
    
    /** eg. jdbc:hsqldb:mem:. */
    String DB_CONNECTION_URL = "db.connection.url";
    
    /** Username for db connection */
    String DB_CONNECTION_USERNAME = "db.connection.username";
    
    /** Password for db connection */
    String DB_CONNECTION_PASSWORD = "db.connection.password";
    
    

}
