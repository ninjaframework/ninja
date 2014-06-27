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

 package threads;

