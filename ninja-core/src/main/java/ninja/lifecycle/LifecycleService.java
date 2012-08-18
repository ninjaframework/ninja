/**
 * Copyright (C) 2012 the original author or authors.
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

package ninja.lifecycle;

import com.google.inject.ImplementedBy;

/**
 * Responsible for starting/stopping the application
 *
 * @author James Roper
 */
@ImplementedBy(LifecycleServiceImpl.class)
public interface LifecycleService {
    /**
     * Start the application
     */
    void start();

    /**
     * Stop the application
     */
    void stop();

    /**
     * Whether the application is started
     *
     * @return True if the application is started
     */
    public boolean isStarted();

    /**
     * Get the state of the lifecycle
     *
     * @return The state
     */
    public State getState();

    /**
     * Get the time that the service has been up for
     *
     * @return The time that the service has been up for
     */
    public long getUpTime();
}
