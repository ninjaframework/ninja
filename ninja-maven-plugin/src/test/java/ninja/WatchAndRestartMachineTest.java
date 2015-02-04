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

package ninja;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;


public class WatchAndRestartMachineTest {
    
    /**
     * Test of checkIfMatchesPattern method, of class WatchAndRestartMachine.
     */
    @Test
    public void testCheckIfMatchesPatternAndAssetsAreIgnored() {
        
        List<String> patterns 
                = Lists.newArrayList(
                        NinjaMavenPluginConstants.DEFAULT_EXCLUDE_PATTERNS);
        
        assertThat(
                WatchAndRestartMachine.checkIfMatchesPattern(
                        patterns, 
                        "target" 
                        + File.separator 
                        + "classes" 
                        + File.separator 
                        + "assets" 
                        + File.separator 
                        + "js" 
                        + File.separator 
                        + "script.js"),
                is(true));
        
        assertThat(
                WatchAndRestartMachine.checkIfMatchesPattern(
                        patterns, 
                        File.separator 
                        + "assets" 
                        + File.separator),
                is(true));
        
    }
    
    
    @Test
    public void testCheckIfMatchesPatternMachtesOfOtherStuff() {
        
        List<String> patterns = Lists.newArrayList(
                NinjaMavenPluginConstants.DEFAULT_EXCLUDE_PATTERNS);
        
        assertThat(
                WatchAndRestartMachine.checkIfMatchesPattern(
                        patterns, 
                        "target" 
                        + File.separator 
                        + "classes" 
                        + File.separator 
                        + "completelyDifferentPath"),
                is(false));

        
    }
    
    
    @Test
    public void testCheckIfMatchesPatternAndFtlHtmlFilesAreIgnored() {
        
        List<String> patterns = Lists.newArrayList(
                NinjaMavenPluginConstants.DEFAULT_EXCLUDE_PATTERNS);

        
        assertThat(
                WatchAndRestartMachine.checkIfMatchesPattern(
                        patterns, 
                        "target" 
                        + File.separator 
                        + "classes" 
                        + File.separator 
                        + "views" 
                        + File.separator 
                        + "ApplicationController" 
                        + File.separator 
                        + "index.ftl.html"),
                is(true));
        
        assertThat(
                WatchAndRestartMachine.checkIfMatchesPattern(
                        patterns, 
                        "target" 
                        + File.separator 
                        + "classes" 
                        + File.separator 
                        + "views" 
                        + File.separator 
                        + "ApplicationController" 
                        + File.separator 
                        + "index.ftl.html.bam"),
                is(false));
 
        
    }
    
}
