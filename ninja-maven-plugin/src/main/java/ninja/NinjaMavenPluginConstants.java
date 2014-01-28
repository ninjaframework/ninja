/*
 * Copyright 2013 ra.
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

import java.io.File;
import java.util.regex.Pattern;

import ninja.standalone.NinjaJetty;
import ninja.utils.NinjaConstant;

/**
 *
 * @author ra
 */
public interface NinjaMavenPluginConstants {
    
    String DEFAULT_CLASSES_DIRECTORY = File.separator + "target" + File.separator + "classes";
    
    String [] DEFAULT_EXCLUDE_PATTERNS = {
            "(.*)" + Pattern.quote(File.separator) + NinjaConstant.VIEWS_DIR + Pattern.quote(File.separator) + "(.*)ftl\\.html$",
            "(.*)" + Pattern.quote(File.separator) + AssetsController.ASSETS_DIR + Pattern.quote(File.separator) + "(.*)"
        };
    
    String USER_DIR = "user.dir";
    
    String NINJA_JETTY_CLASSNAME = NinjaJetty.class.getName();
    
}
