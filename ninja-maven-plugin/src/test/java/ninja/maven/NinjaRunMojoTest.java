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

package ninja.maven;

import ninja.build.RunClassInSeparateJvmMachine;
import ninja.build.WatchAndRestartMachine;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.codehaus.plexus.PlexusTestCase.getTestFile;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit test a Maven plugin in a modern way.
 */
@RunWith(MockitoJUnitRunner.class)
public class NinjaRunMojoTest {

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() {
        }

        @Override
        protected void after() {
        }
    };

    @Mock
    private MavenProject project;
    
    @Mock
    private RunClassInSeparateJvmMachine machine;
    
    @Mock
    private WatchAndRestartMachine watcher;
    
    @Captor
    private ArgumentCaptor<String> classNameWithMainToRunCaptor;
    
    @Captor
    private ArgumentCaptor<ArrayList<String>> classpathCaptor;
    
    @Captor
    private ArgumentCaptor<ArrayList<String>> jvmArgumentsCaptor;

    private NinjaRunMojo ninjaRunMojo;
    
    @Before
    public final void before() throws Exception {
        File pom = getTestFile("src/test/resources/projects/minimal/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        
        NinjaRunMojo mojo = (NinjaRunMojo)rule.lookupMojo("run", pom);
        assertNotNull(mojo);
        
        ninjaRunMojo = spy(mojo);
    }
    
    private void setupMachineAndWatcherStubs() throws IOException {
        doReturn(machine)
            .when(ninjaRunMojo)
            .buildRunClassInSeparateJvmMachine(anyString(), classNameWithMainToRunCaptor.capture(), classpathCaptor.capture(), jvmArgumentsCaptor.capture(), any());
            
        doReturn(watcher)
            .when(ninjaRunMojo)
            .buildWatchAndRestartMachine(anySet(), anySet(), anySet(), any());
    }
    
    @Test
    public void alertAndStopExecutionIfDirectoryWithCompiledClassesOfThisProjectDoesNotExist() {
        ninjaRunMojo.buildOutputDirectory = "target/doesnotexist";
        
        try {
            ninjaRunMojo.execute();
            fail();
        } catch (MojoExecutionException e) {
            assertThat(e.getMessage(), containsString("Directory with classes does not exist"));
        }
    }
    
    @Test
    public void minimal() throws Exception {
        ninjaRunMojo.buildOutputDirectory = "target/classes";
        ninjaRunMojo.project = project;
        ninjaRunMojo.pluginArtifacts = new ArrayList<>();
        ninjaRunMojo.settleDownMillis = 500L;
        ninjaRunMojo.mode = "dev";
        
        setupMachineAndWatcherStubs();
        
        ninjaRunMojo.execute();
        
        verify(machine, times(1)).restart();
        verify(watcher, times(1)).run();
        
        // verify what actually got to ninja machine
        assertThat(jvmArgumentsCaptor.getValue().get(0), is("-Dninja.mode=dev"));
    }
    
    @Test
    public void portProperty() throws Exception {
        ninjaRunMojo.buildOutputDirectory = "target/classes";
        ninjaRunMojo.project = project;
        ninjaRunMojo.pluginArtifacts = new ArrayList<>();
        ninjaRunMojo.settleDownMillis = 500L;
        ninjaRunMojo.mode = "dev";
        ninjaRunMojo.port = 9000;
        
        setupMachineAndWatcherStubs();
        
        ninjaRunMojo.execute();
        
        verify(machine, times(1)).restart();
        verify(watcher, times(1)).run();
        
        // verify what actually got to ninja machine
        assertThat(jvmArgumentsCaptor.getValue().get(0), is("-Dninja.mode=dev"));
        assertThat(jvmArgumentsCaptor.getValue().get(1), is("-Dninja.port=9000"));
    }
    
    @Test
    public void contextProperty() throws Exception {
        ninjaRunMojo.buildOutputDirectory = "target/classes";
        ninjaRunMojo.project = project;
        ninjaRunMojo.pluginArtifacts = new ArrayList<>();
        ninjaRunMojo.settleDownMillis = 500L;
        ninjaRunMojo.mode = "dev";
        ninjaRunMojo.context = "/test";
        
        setupMachineAndWatcherStubs();
        
        ninjaRunMojo.execute();
        
        verify(machine, times(1)).restart();
        verify(watcher, times(1)).run();
        
        // verify what actually got to ninja machine
        assertThat(jvmArgumentsCaptor.getValue().get(0), is("-Dninja.mode=dev"));
        assertThat(jvmArgumentsCaptor.getValue().get(1), is("-Dninja.context=/test"));
    }
    
    @Test
    public void contextPropertyFallbackToContextPath() throws Exception {
        ninjaRunMojo.buildOutputDirectory = "target/classes";
        ninjaRunMojo.project = project;
        ninjaRunMojo.pluginArtifacts = new ArrayList<>();
        ninjaRunMojo.settleDownMillis = 500L;
        ninjaRunMojo.mode = "dev";
        // verify old name still works
        ninjaRunMojo.contextPath = "/test";
        
        setupMachineAndWatcherStubs();
        
        ninjaRunMojo.execute();
        
        verify(machine, times(1)).restart();
        verify(watcher, times(1)).run();
        
        // verify what actually got to ninja machine
        assertThat(jvmArgumentsCaptor.getValue().get(0), is("-Dninja.mode=dev"));
        assertThat(jvmArgumentsCaptor.getValue().get(1), is("-Dninja.context=/test"));
    }
    
    @Test
    public void mainClassProperty() throws Exception {
        ninjaRunMojo.buildOutputDirectory = "target/classes";
        ninjaRunMojo.project = project;
        ninjaRunMojo.pluginArtifacts = new ArrayList<>();
        ninjaRunMojo.settleDownMillis = 500L;
        ninjaRunMojo.mode = "dev";
        ninjaRunMojo.mainClass = "this.is.a.class.to.run";
        
        setupMachineAndWatcherStubs();
        
        ninjaRunMojo.execute();
        
        verify(machine, times(1)).restart();
        verify(watcher, times(1)).run();
        
        // verify what actually got to ninja machine
        assertThat(classNameWithMainToRunCaptor.getValue(), is("this.is.a.class.to.run"));
    }
    
    @Test
    public void jvmArgsProperty() throws Exception {
        ninjaRunMojo.buildOutputDirectory = "target/classes";
        ninjaRunMojo.project = project;
        ninjaRunMojo.pluginArtifacts = new ArrayList<>();
        ninjaRunMojo.settleDownMillis = 500L;
        ninjaRunMojo.mode = "dev";
        ninjaRunMojo.jvmArgs = "\"This has spaces\" -Dprop=name -XXXX";
        
        setupMachineAndWatcherStubs();
        
        ninjaRunMojo.execute();
        
        verify(machine, times(1)).restart();
        verify(watcher, times(1)).run();
        
        // verify what actually got to ninja machine
        ArrayList<String> value = jvmArgumentsCaptor.getValue();
        assertThat(value.get(0), is("-Dninja.mode=dev"));
        assertThat(value.get(1), is("This has spaces"));
        assertThat(value.get(2), is("-Dprop=name"));
        assertThat(value.get(3), is("-XXXX"));
    }
    
}
