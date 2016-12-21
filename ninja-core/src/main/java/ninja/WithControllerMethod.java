/*
 * Copyright 2016 ninjaframework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja;

import ninja.ControllerMethods.ControllerMethod;
import ninja.ControllerMethods.ControllerMethod0;
import ninja.ControllerMethods.ControllerMethod1;
import ninja.ControllerMethods.ControllerMethod10;
import ninja.ControllerMethods.ControllerMethod2;
import ninja.ControllerMethods.ControllerMethod3;
import ninja.ControllerMethods.ControllerMethod4;
import ninja.ControllerMethods.ControllerMethod5;
import ninja.ControllerMethods.ControllerMethod6;
import ninja.ControllerMethods.ControllerMethod7;
import ninja.ControllerMethods.ControllerMethod8;
import ninja.ControllerMethods.ControllerMethod9;

/**
 * Interface that exposes multiple with methods that accept a large number of
 * various argument combinations.
 * @param <T> The result to return
 */
public interface WithControllerMethod<T> {
    
    T with(ControllerMethod controllerMethod);
    
    default T with(ControllerMethod0 controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A> T with(ControllerMethod1<A> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A,B> T with(ControllerMethod2<A,B> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A,B,C> T with(ControllerMethod3<A,B,C> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A,B,C,D> T with(ControllerMethod4<A,B,C,D> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A,B,C,D,E> T with(ControllerMethod5<A,B,C,D,E> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A,B,C,D,E,F> T with(ControllerMethod6<A,B,C,D,E,F> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A,B,C,D,E,F,G> T with(ControllerMethod7<A,B,C,D,E,F,G> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A,B,C,D,E,F,G,H> T with(ControllerMethod8<A,B,C,D,E,F,G,H> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A,B,C,D,E,F,G,H,I> T with(ControllerMethod9<A,B,C,D,E,F,G,H,I> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
    default <A,B,C,D,E,F,G,H,I,J> T with(ControllerMethod10<A,B,C,D,E,F,G,H,I,J> controllerMethod) {
        return with((ControllerMethod)controllerMethod);
    }
    
}
