/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja.maven;

import ninja.AssetsController;
import ninja.standalone.NinjaJetty;
import ninja.utils.NinjaConstant;

/**
 *
 * @author ra
 */
public interface NinjaMavenPluginConstants {
    
    public String SEPARATOR = "/"; 
    
    String [] DEFAULT_EXCLUDE_PATTERNS = {
            "(.*)" + SEPARATOR + NinjaConstant.VIEWS_DIR + SEPARATOR + "(.*)\\.html$",
            "(.*)" + SEPARATOR + AssetsController.ASSETS_DIR + SEPARATOR + "(.*)"
        };
    
    /** 
     * Base directory where the ninja:run mode has its root.
     * Is the place where the project pom.xml is.
     */
    String USER_DIR = "user.dir";
    
    String NINJA_JETTY_CLASSNAME = NinjaJetty.class.getName();
    
    public String NINJA_STANDALONE_ARTIFACT_ID = "ninja-standalone";
    
}
