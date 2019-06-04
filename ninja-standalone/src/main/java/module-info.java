module ninja.standalone {
	requires com.google.guice;
	requires org.eclipse.jetty.servlet;
	requires org.eclipse.jetty.server;
	requires org.eclipse.jetty.http;
	requires org.eclipse.jetty.util;
	requires org.eclipse.jetty.websocket.javax.websocket.server;
	requires ninja.servlet;
	requires ninja.core;
	requires java.xml;
	requires javax.servlet.api;
	requires slf4j.api;
	requires commons.lang3;
	requires org.eclipse.jetty.xml;

	exports standalone;
	exports standalone.console;
}
