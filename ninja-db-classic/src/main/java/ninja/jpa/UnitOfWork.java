/**
 * Copyright (C) the original author or authors.
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

package ninja.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is the equivalent to @Transactional without transactions.
 * 
 * IMPORTANT:
 * Not using @UnitOfWork or @Transactional when accessing your database is 
 * not recommended. If you do so you might end up with a lot of open connections.
 * 
 * The difference between @Transactional and @UnitOfWork is that @UnitOfWork
 * does not open or commit any transactions. This may be faster for simple reads. 
 * But if you alter data you should use @Transactional.
 * 
 * You can use UnitOfWork simultaneously on many levels (methods, classes etc).
 * But only the most outer declaration will open a unit of work. There us no
 * nesting of unit of works taking place.
 * 
 * You can mix @UnitOfWork and @Transactional. UnitOfWork will keep the unit open,
 * and all levels annotated with @Transactional will open and commit transactions.
 * 
 * @author Raphael A. Bauer
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UnitOfWork {

}
