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

package yoda.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This can be used as an uncaught exception handler which will log an uncaught exception as a severe log message.
 * 
 * @author dhudson - created 16 Jun 2014
 * @since 1.0
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionHandler.class);  

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        StringBuilder sb = new StringBuilder(100);
        sb.append("Uncaught Exception");
        if (t != null) {
            sb.append(" in thread ");
            sb.append(t.getName());
        }

        logger.error(sb.toString(), e);
    }

}
