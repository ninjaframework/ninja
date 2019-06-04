module ninja.websockets.jsr356 {
	requires transitive jakarta.websocket.server;
	requires transitive jakarta.websocket.client;
	requires com.google.guice;
	requires javax.inject;
	requires ninja.core;
	requires slf4j.api;

	exports ninja.websockets.jsr356;
}
