/**
 * Copyright (C) 2012-2020 the original author or authors.
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

package ninja;

import java.io.Serializable;

/**
 * Functional interfaces for Ninja controller methods accepting up to X
 * number of arguments with type inference.
 */
public class ControllerMethods {
    
    /**
     * Marker interface that all functional interfaces will extend.  Useful for
     * simple validation an object is a ControllerMethod.
     */
    static public interface ControllerMethod extends Serializable { }
    
    @FunctionalInterface
    static public interface ControllerMethod0 extends ControllerMethod {
        Result apply() throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod1<A> extends ControllerMethod {
        Result apply(A a) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod2<A,B> extends ControllerMethod {
        Result apply(A a, B b) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod3<A,B,C> extends ControllerMethod {
        Result apply(A a, B b, C c) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod4<A,B,C,D> extends ControllerMethod {
        Result apply(A a, B b, C c, D d) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod5<A,B,C,D,E> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod6<A,B,C,D,E,F> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod7<A,B,C,D,E,F,G> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod8<A,B,C,D,E,F,G,H> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod9<A,B,C,D,E,F,G,H,I> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod10<A,B,C,D,E,F,G,H,I,J> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod11<A,B,C,D,E,F,G,H,I,J,K> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod12<A,B,C,D,E,F,G,H,I,J,K,L> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod13<A,B,C,D,E,F,G,H,I,J,K,L,M> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod14<A,B,C,D,E,F,G,H,I,J,K,L,M,N> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n) throws Exception;
    }
    
    @FunctionalInterface
    static public interface ControllerMethod15<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O> extends ControllerMethod {
        Result apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o) throws Exception;
    }
    
    // if you need more than 15 arguments then we recommend using the
    // legacy Class, methodName strategy
    
    // helper methods to allow classes to accept `ControllerMethod` but still
    // have the compiler create the correct functional method under-the-hood
    
    static public ControllerMethod0 of(ControllerMethod0 functionalMethod) {
        return functionalMethod;
    }
    
    static public <A> ControllerMethod1<A> of(ControllerMethod1<A> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B> ControllerMethod2<A,B> of(ControllerMethod2<A,B> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C> ControllerMethod3<A,B,C> of(ControllerMethod3<A,B,C> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D> ControllerMethod4<A,B,C,D> of(ControllerMethod4<A,B,C,D> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E> ControllerMethod5<A,B,C,D,E> of(ControllerMethod5<A,B,C,D,E> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F> ControllerMethod6<A,B,C,D,E,F> of(ControllerMethod6<A,B,C,D,E,F> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F,G> ControllerMethod7<A,B,C,D,E,F,G> of(ControllerMethod7<A,B,C,D,E,F,G> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F,G,H> ControllerMethod8<A,B,C,D,E,F,G,H> of(ControllerMethod8<A,B,C,D,E,F,G,H> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F,G,H,I> ControllerMethod9<A,B,C,D,E,F,G,H,I> of(ControllerMethod9<A,B,C,D,E,F,G,H,I> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F,G,H,I,J> ControllerMethod10<A,B,C,D,E,F,G,H,I,J> of(ControllerMethod10<A,B,C,D,E,F,G,H,I,J> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F,G,H,I,J,K> ControllerMethod11<A,B,C,D,E,F,G,H,I,J,K> of(ControllerMethod11<A,B,C,D,E,F,G,H,I,J,K> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F,G,H,I,J,K,L> ControllerMethod12<A,B,C,D,E,F,G,H,I,J,K,L> of(ControllerMethod12<A,B,C,D,E,F,G,H,I,J,K,L> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F,G,H,I,J,K,L,M> ControllerMethod13<A,B,C,D,E,F,G,H,I,J,K,L,M> of(ControllerMethod13<A,B,C,D,E,F,G,H,I,J,K,L,M> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F,G,H,I,J,K,L,M,N> ControllerMethod14<A,B,C,D,E,F,G,H,I,J,K,L,M,N> of(ControllerMethod14<A,B,C,D,E,F,G,H,I,J,K,L,M,N> functionalMethod) {
        return functionalMethod;
    }
    
    static public <A,B,C,D,E,F,G,H,I,J,K,L,M,N,O> ControllerMethod15<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O> of(ControllerMethod15<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O> functionalMethod) {
        return functionalMethod;
    }
    
}