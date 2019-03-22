open module ninja.servlet {
	exports ninja.servlet;
	requires javax.servlet.api;
	requires ninja.core;
	requires com.google.guice;
	requires com.google.guice.extensions.servlet;
	requires javax.websocket.api;
	requires slf4j.api;
	requires ninja.websockets.jsr356;
	requires commons.fileupload;
	requires com.google.common;
	requires jsr305;
}