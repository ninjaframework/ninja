package ninja.servlet.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ninja.params.WithArgumentExtractor;

@WithArgumentExtractor(RequestExtractor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface Request {
    // Just a marker interface
}