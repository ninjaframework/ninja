/**
 * Copyright (C) 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.utils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.ControllerMethods.ControllerMethod1;
import ninja.ControllerMethods.ControllerMethod2;
import ninja.utils.Lambdas.Kind;
import ninja.utils.Lambdas.LambdaInfo;
import static org.hamcrest.CoreMatchers.startsWith;
import org.junit.Ignore;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class LambdasTest {

    @FunctionalInterface
    static public interface Function1<T,R> extends Serializable {
        R apply(T t);
    }
    
    @FunctionalInterface
    static public interface Function2<C,T,R> extends Serializable { 
        R apply(C c, T t);
    }
    
    static private String longToString(Long value) {
        return value.toString();
    }
    
    @Test
    public void staticMethodReference() throws Exception {
        Function1<Long,String> lambda = LambdasTest::longToString;
        
        LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
        
        assertThat(lambdaInfo.getKind(), is(Kind.STATIC_METHOD_REFERENCE));
        
        SerializedLambda serializedLambda = lambdaInfo.getSerializedLambda();
        
        assertThat(serializedLambda.getFunctionalInterfaceMethodName(), is("apply"));
        assertThat(serializedLambda.getImplClass().replace('/', '.'), is(LambdasTest.class.getCanonicalName()));
        assertThat(serializedLambda.getImplMethodName(), is("longToString"));
        assertThat(serializedLambda.getImplMethodKind(), is(6));    // 6 = static method
        assertThat(serializedLambda.getCapturedArgCount(), is(0));
        
        // verify it can be dynamically invoked
        String value = (String)lambdaInfo.getImplementationMethod().invoke(null, 1L);
        
        assertThat(value, is("1"));
    }
    
    static public class Calculator {
        private final Long initial;

        public Calculator(Long initial) {
            this.initial = initial;
        }
        
        public String l2s(Long value) {
            long calculated = initial + value;
            return Long.toString(calculated);
        }
    }
    
    @Test
    public void specificInstanceMethodReference() throws Exception {
        Calculator calc = new Calculator(1L);
        
        Function1<Long,String> lambda = calc::l2s;
        
        LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
        
        assertThat(lambdaInfo.getKind(), is(Kind.SPECIFIC_INSTANCE_METHOD_REFERENCE));
        
        SerializedLambda serializedLambda = lambdaInfo.getSerializedLambda();
        
        assertThat(serializedLambda.getFunctionalInterfaceMethodName(), is("apply"));
        assertThat(serializedLambda.getImplClass().replace('/', '.').replace('$', '.'), is(Calculator.class.getCanonicalName()));
        assertThat(serializedLambda.getImplMethodName(), is("l2s"));
        assertThat(serializedLambda.getImplMethodSignature(), is("(Ljava/lang/Long;)Ljava/lang/String;"));
        assertThat(serializedLambda.getCapturedArgCount(), is(1));  // captured "this"
        assertThat(serializedLambda.getCapturedArg(0), is(calc));   // captured "this"
        
        // verify it can be dynamically invoked
        String value = (String)lambdaInfo.getFunctionalMethod().invoke(lambda, 1L);
        //String value = (String)lambda.getClass().getMethod("apply", Long.class).invoke(calc, 1L);
        
        assertThat(value, is("2"));
    }
    
    private Result home() {
        return Results.html().renderRaw("Hi".getBytes(StandardCharsets.UTF_8));
    }
    
    @Test
    public void anyInstanceMethodReference() throws Exception {
        ControllerMethod1<LambdasTest> lambda = LambdasTest::home;
        
        LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
        
        assertThat(lambdaInfo.getKind(), is(Kind.ANY_INSTANCE_METHOD_REFERENCE));
        
        SerializedLambda serializedLambda = lambdaInfo.getSerializedLambda();
        
        assertThat(serializedLambda.getFunctionalInterfaceMethodName(), is("apply"));
        assertThat(serializedLambda.getImplClass().replace('/', '.'), is(LambdasTest.class.getCanonicalName()));
        assertThat(serializedLambda.getImplMethodName(), is("home"));
        assertThat(serializedLambda.getImplMethodSignature(), is("()Lninja/Result;"));
        assertThat(serializedLambda.getCapturedArgCount(), is(0));
    }
    
    @Test
    public void anonymousClassReference() throws Exception {
        @SuppressWarnings("Convert2Lambda")
        ControllerMethod1<Context> lambda = new ControllerMethod1<Context>() {
            @Override
            public Result apply(Context a) {
                return Results.html().renderRaw("".getBytes(StandardCharsets.UTF_8));
            }
        };
        
        try {
            LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
    
    @Test
    public void anonymousMethodReference() throws Exception {
        ControllerMethod1<Context> lambda = (Context context) -> Results.html().renderRaw("".getBytes(StandardCharsets.UTF_8));
        
        LambdaInfo lambdaInfo = Lambdas.reflect(lambda);
        
        assertThat(lambdaInfo.getKind(), is(Kind.ANONYMOUS_METHOD_REFERENCE));
        
        SerializedLambda serializedLambda = lambdaInfo.getSerializedLambda();
        
        assertThat(serializedLambda.getFunctionalInterfaceMethodName(), is("apply"));
        assertThat(serializedLambda.getImplClass().replace('/', '.'), is(LambdasTest.class.getCanonicalName()));
        assertThat(serializedLambda.getImplMethodName(), startsWith("lambda$"));
        assertThat(serializedLambda.getInstantiatedMethodType(), is("(Lninja/Context;)Lninja/Result;"));
        // includes captured args btw...
        assertThat(serializedLambda.getImplMethodSignature(), is("(Lninja/Context;)Lninja/Result;"));
        assertThat(serializedLambda.getImplMethodKind(), is(6));    // 6 = REF_invokeStatic
        assertThat(serializedLambda.getCapturedArgCount(), is(0));
    }

}
