//package conf;
//
//import ninja.servlet.NinjaServletDispatcher;
//
//public class ServletModule extends com.google.inject.servlet.ServletModule {
//
//    @Override
//    protected void configureServlets() {
//
//        bind(NinjaServletDispatcher.class).asEagerSingleton();
//        serve("/*").with(NinjaServletDispatcher.class);
//        
//        
//    }
//
//}