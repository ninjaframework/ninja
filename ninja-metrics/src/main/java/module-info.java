module ninja.metrics {
	exports ninja.metrics;
	requires com.google.guice;
	requires aopalliance;
	requires metrics.core;
	requires slf4j.api;
	requires ninja.core;
	requires metrics.jvm;
	requires logback.classic;
	requires metrics.logback;
	requires com.google.common;
}
