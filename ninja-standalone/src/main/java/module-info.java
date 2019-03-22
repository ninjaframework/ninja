module ninja.standalone {
	requires com.google.guice;
	requires jetty.server;
	requires ninja.servlet;
	requires jetty.servlet;
	requires javax.servlet.api;
	requires jetty.http;
	requires jetty.util;
	requires javax.websocket.server.impl;
	requires jetty.xml;
	requires slf4j.api;
	requires ninja.core;
}