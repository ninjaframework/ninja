Console Application
===================

## Introduction

Ninja is designed to be a web framework, but if you want to leverage its 
excellent dependency injection or configuration for running a webserver-less
console application, Ninja v6.3.0+ added support for `NinjaConsole`
as a main entry point.

Your application will start like it normally would if running in a web server
container like Jetty, but will not start a web server and will not initialize
your `conf.Router`. All other Guice configuration and lifecycle methods will
be started as they normally would if running as a standard Ninja web application.

## Usage

You can either start the JVM directly or customize `NinjaConsole`.
Since it includes a main method, the following would start your console-based
Ninja application:

<pre class="prettyprint">
java -cp &lt;classpath-here&gt; NinjaConsole
</pre>

Or you can write your own main method and customize `NinjaConsole` as much
as you need:

<pre class="prettyprint">
import NinjaConsole;
import ninja.utils.NinjaMode;

public class MyMain {
 
    static public void main(String[] args) throws Exception {
        NinjaConsole ninja = new NinjaConsole()
            .ninjaMode(NinjaMode.prod)
            .start();

        // other code (e.g. access guice injector)
        // ninja.getInjector();

        ninja.shutdown();
    }
    
}
</pre>