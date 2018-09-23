WebSockets
==========

## Introduction

The WebSocket Protocol enables two-way communication between a client and server.
The protocol consists of an opening handshake followed by basic message framing,
layered over TCP.  The goal of this technology is to provide a mechanism for
browser-based applications that need two-way communication with servers that does
not rely on opening multiple HTTP connections.

Ninja v6.2.0+ includes comprehensive support for WebSockets.  While designed to
work with the standard Java WebSocket standard (JSR-356), Ninja includes a number
of useful features beyond JSR-356 to make working with web sockets simpler.

## Setup

Make sure you're running Ninja 6.2.0+. Ninja WebSockets will work out-of-the-box
in the Jetty standalone.  If running your Ninja application as a WAR.  Ninja
WebSockets will also work in any servlet container that supports JSR-356.
Ninja's implementation has been tested against Jetty 9.3.15+, Tomcat 7.0.81+,
and Wildfly 10+.

If running as a WAR in a servlet container, you'll need to swap your `GuiceFilter`
and use `ninja.servlet.NinjaServletFilter`. Or you can simply use Ninja's
automatic servlet configuration via its `ServletContainerInitializer` support
and omit even having a `web.xml`.

## Add a WebSocket route

All WebSocket endpoints will be configured in Ninja's router.  A new pseudo
HTTP method of `WS` indicates a route is for a WebSocket.  The controller
method that handles it will be able to accept/reject the handshake and negotiate
the protocol and extensions.  If you've ever used standard JSR-356 WebSockets
you'll appreciate how much easier Ninja's implementation is.

<pre class="prettyprint">
public class Routes implements ApplicationRoutes {
    @Override
    public void init(Router router) {

        router.WS().route("/echo").with(EchoWebSocket::handshake);
        router.WS().route("/chats/{id}").with(ChatWebSocket::handshake);

    }
}
</pre>

Note that you'll be slightly limited with the URI you can use depending
on what's providing your underlying Ninja WebSocket support. If using standard
JSR-356 WebSockets (almost always the case), then you'll be able to use simple path
parameters such as `/chat/{id}`, but no regexes such as `/chat/{id: .*}`.

The declaring class of the handshake method you specify in your route will 
become the WebSocket endpoint.  If you're using JSR-356 WebSockets then that
means your controller class will need to implement the `javax.websocket.Endpoint`
interface.  See the code below for a full example.

## Web socket controller/endpoint

You will want to make sure you do not mark your WebSocket class as a `@Singleton`.
Unless you know what you are doing, its better to create a new instance for each
WebSocket session that is created.

Your controller/endpoint will be created using Guice -- so the `@Inject` annotation
will be honored when instantiating your endpoint.

## WebSocket handshake

One of the issues with Java's JSR-356 standard is that you have very little
control with the initial handshake request and little access to the HTTP headers
sent by the client.  This design flaw would make using Ninja's session or context
impossible.

Ninja fixes this design flaw with JSR-356 by using a two-step process to 
complete the handshake.  When a client initiates a WebSocket request,
Ninja will detect the attempt, instantiate your WebSocket endpoint, and call
your `handshake` controller method (like any standard HTTP GET request).  You
can then accept/reject the handshake request or save any variables you'll want
to access later once the WebSocket session is established.

<pre class="prettyprint">
public class ChatWebSocket extends Endpoint implements MessageHandler.Whole&lt;String&gt; {
    
    public Result handshake(Context context, WebSocketHandshake handshake) {
        // negotiate the protocol (not always used by clients)
        handshake.selectProtocol("chat");

        // process handshake, save whatever you need, tell ninja to proceed
        // or return a failure result to reject the request
        return Results.webSocketContinue();
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // jsr-356 stuff here...
    }

</pre>

If your `handshake` method returns a status code of 101 then Ninja will
let the underlying HTTP container proceed with upgrading the connection to a
WebSocket.  If the upgrade is successful, Ninja guarantees that the instance
created for your handshake will be the same one that will receive the `onOpen`
event.

## WebSocket session

Once the handshake completes and a WebSocket session is established, you will
then handle processing like any other JSR-356 implementation.  There are any
number of resources on the Internet for examples.