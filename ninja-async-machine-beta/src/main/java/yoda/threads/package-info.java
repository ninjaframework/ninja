/**
 * Copyright (C) 2012-2016 the original author or authors.
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

/**
 * The thread package is responsible for the Responder thread pool.
 * <p>
 * The responder thread pool is used if Yoda is configured in Async mode.
 * To do this simply use <code>bind(NinjaImpl.class).to(YodaAsyncImpl.class).in(Singleton.class); </code> in Module.
 * The responder execution handler is controlled from application.conf.
 * 
 * The idea here is that has requests come in form the Servlet Container (Jetty et al), that Async servlets are used.
 * The configuration allows for the queue size, thread pool size and request timeout.
 * If the queue is full, then a response of Too Many Requests is returned (429).
 * If the request has been on the queue for too long then Request Timeout will be returned (408).
 * This gives far more control and able to marshal requests correctly.
 * 
 * @since 1.0
 */

 package yoda.threads;

