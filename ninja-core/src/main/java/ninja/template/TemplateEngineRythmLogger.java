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

package ninja.template;

import org.rythmengine.logger.ILogger;
import org.slf4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class TemplateEngineRythmLogger implements ILogger {

    public final Logger logger;

    public TemplateEngineRythmLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String format, Object... args) {
        logger.trace(msg_(format, args));
    }

    @Override
    public void trace(Throwable t, String format, Object... args) {
        logger.trace(msg_(format, args), t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String format, Object... args) {
        logger.debug(msg_(format, args));
    }

    @Override
    public void debug(Throwable t, String format, Object... args) {
        logger.debug(msg_(format, args), t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String format, Object... arg) {
        logger.info(msg_(format, arg));
    }

    @Override
    public void info(Throwable t, String format, Object... args) {
        logger.info(msg_(format, args), t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String format, Object... arg) {
        logger.warn(msg_(format, arg));
    }

    @Override
    public void warn(Throwable t, String format, Object... args) {
        logger.warn(msg_(format, args), t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String format, Object... arg) {
        logger.error(msg_(format, arg));
    }

    @Override
    public void error(Throwable t, String format, Object... args) {
        logger.error(msg_(format, args), t);
    }

    private static String msg_(String msg, Object... args) {
        return String.format("RythmTemplate " + "> %1$s",
                String.format(msg, args));
    }
}
