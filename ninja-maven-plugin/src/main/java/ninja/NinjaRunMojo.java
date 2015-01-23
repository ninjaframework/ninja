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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ninja.standalone.NinjaJetty;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.common.collect.Lists;

/**
 * Starts Ninja's SuperDevMode.
 * 
 * @goal run
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class NinjaRunMojo extends AbstractMojo {
    
    /**
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject mavenProject;
    
    /**
     * Directory containing the build files.
     * 
     * For webapps this is usually
     * something like /User/username/workspace/project/target/classes
     * 
     * @parameter property="project.build.outputDirectory"
     */
    private String buildOutputDirectory;
    
    /** 
     * @parameter default-value="${plugin.artifacts}" 
     */
    private java.util.List<org.apache.maven.artifact.Artifact> pluginArtifacts;
    
    Plugin plugin;
    
    /**
     * Exludes in Java regex format. If you want to exclude all
     * freemarker templates use something like (.*)ftl.html$ for instance.
     * 
     * @parameter
     */
    protected String [] excludes;
    
    private List<String> excludesAsList = Lists.newArrayList();
    
    /**
     * Adds assets directory and freemarker templates to excluded files.
     * These are loaded from the src directory in dev mode.
     * 
     * Default excludes are: 
     * - "(.*)ftl\\.html$"
     * - "File.pathSeparator + "assets" + File.separator"
     * 
     * @parameter default-value="true"
     */
    protected boolean useDefaultExcludes;
    
    /**
     * Context path for SuperDevMode.
     * 
     * @parameter
     */
    private String contextPath;

    /**
    * Port for SuperDevMode
    *
    * @parameter
    */
    private String port;

    @Override
    public void execute() throws MojoExecutionException {
        
        // If not set up in pom.xml then use system property.
        if (contextPath == null) {
            String contextPathProperty 
                    = System.getProperty(
                        NinjaJetty.COMMAND_LINE_PARAMETER_NINJA_CONTEXT);
            
            contextPath = contextPathProperty;
        }



        if (port == null) {
            String portProperty
                    = System.getProperty(
                        NinjaJetty.COMMAND_LINE_PARAMETER_NINJA_PORT, "8080");
            port = portProperty;
        }

        
        getLog().debug(
                "Directory for classes is (used to start local jetty and watch for changes: " 
                + buildOutputDirectory);
        
        
        getLog().info("------------------------------------------------------------------------");
        if (contextPath != null) {
            getLog().info("Launching Ninja SuperDevMode with '" + contextPath + "' context path.");
        } else {
             getLog().info("Launching Ninja SuperDevMode on root context");
        }
        getLog().info("Ninja will launch on port: " + port);
        getLog().info("------------------------------------------------------------------------");
        

        initMojoFromUserSubmittedParameters();
        

        List<String> classpathItems = Lists.newArrayList();
        
        
        alertAndStopExecutionIfDirectoryWithCompiledClassesOfThisProjectDoesNotExist(
            buildOutputDirectory);
        
        classpathItems.add(buildOutputDirectory);

       
        for (org.apache.maven.artifact.Artifact artifact : mavenProject.getArtifacts()) {
            classpathItems.add(artifact.getFile().toString());           
        }       
        
        List<Artifact> allArtifactsFromNinjaStandaloneInPlugin 
            = getAllArtifactsComingFromNinjaStandalone(pluginArtifacts);
        
        for (Artifact artifact : allArtifactsFromNinjaStandaloneInPlugin) {
            
            //only add once...
            if (!classpathItems.contains(artifact.getFile().toString())) {
                classpathItems.add(artifact.getFile().toString());
            }
        
        }       
                
        
        Path directoryToWatchRecursivelyForChanges 
                = FileSystems.getDefault().getPath(
                        buildOutputDirectory);
        
        try {
            
            WatchAndRestartMachine nWatchAndTerminate = new WatchAndRestartMachine(
                    NinjaMavenPluginConstants.NINJA_JETTY_CLASSNAME,
                    directoryToWatchRecursivelyForChanges,
                    classpathItems,
                    excludesAsList, 
                    contextPath,
                    port);
            
            nWatchAndTerminate.startWatching();
            
        } catch (IOException e) {
            getLog().error(e);
        }
    }
    
    
    private void initMojoFromUserSubmittedParameters() {
    
        if (excludes != null && excludes.length > 0) {
            excludesAsList.addAll(Arrays.asList(excludes));
            
        }
        
        
        if (useDefaultExcludes) {
        
            excludesAsList.addAll(
                    Arrays.asList(
                        NinjaMavenPluginConstants.DEFAULT_EXCLUDE_PATTERNS));
            
        }
    
    }
    
    /**
     * This might be a bit hacky. But just a little bit.
     * 
     * We have to add ninja-standalone and all of its dependencies to
     * the classpath, so that NinjaJetty can start.
     * 
     * But we do not want the user to declare the dependencies inside his
     * project pom.xml.
     * 
     * Therefore we declare the dependency in this plugin pom.xml.
     * But we do not want to include all of this plugin's dependencies in the
     * classpath for NinjaJetty. Therefore this method filters everything and 
     * only adds  dependencies (also transitive deps) that originate
     * from ninja-standalone. That way we get all deps we need.
     * 
     * @param artifacts A list of artifacts that will be filtered.
     * @return All artifacts coming from artifactId "ninja-standalone" 
     *         (including transitive dependencies)
     */
    private List<org.apache.maven.artifact.Artifact> getAllArtifactsComingFromNinjaStandalone(
        List<org.apache.maven.artifact.Artifact> artifacts) {
    
        List<org.apache.maven.artifact.Artifact> resultingArtifacts = new ArrayList<>();
        
        for (org.apache.maven.artifact.Artifact artifact : artifacts) {
        
            for (String dependencyTrail : artifact.getDependencyTrail()) {
            
                // something like:  org.ninjaframework:ninja-standalone:jar:2.5.2
                if (dependencyTrail.contains(NinjaMavenPluginConstants.NINJA_STANDALONE_ARTIFACT_ID)) {
                
                    resultingArtifacts.add(artifact);
                    break;
                
                }
                
            }
        
        }

        return resultingArtifacts;
    
    }
    
    
    public void alertAndStopExecutionIfDirectoryWithCompiledClassesOfThisProjectDoesNotExist(
        String directoryWithCompiledClassesOfThisProject) {
        
        if (!new File(directoryWithCompiledClassesOfThisProject).exists()) {
            
            getLog().error("Directory with classes does not exist: " + directoryWithCompiledClassesOfThisProject);
            getLog().error("Maybe running 'mvn compile'  before running 'mvn ninja:run' helps :)");
            
            // BAM!
            System.exit(1);
        }
    
    }
            
                

}