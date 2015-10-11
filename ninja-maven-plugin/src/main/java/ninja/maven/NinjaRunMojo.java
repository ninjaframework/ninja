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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ninja.build.DelayedRestartTrigger;
import ninja.build.RunClassInSeparateJvmMachine;
import ninja.build.WatchAndRestartMachine;
import ninja.standalone.NinjaJetty;
import ninja.utils.NinjaConstant;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Starts Ninja's SuperDevMode.
 */
@Mojo(name = "run",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        defaultPhase = LifecyclePhase.NONE,
        threadSafe = true)
public class NinjaRunMojo extends AbstractMojo {
    
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;
    
    @Parameter(defaultValue = "${plugin.artifacts}", readonly = true)
    private List<org.apache.maven.artifact.Artifact> pluginArtifacts;

    /**
     * Skip execution of this plugin.
     */
    @Parameter(property = "ninja.skip", defaultValue="false", required = true)
    private boolean skip;
    
    /**
     * Directory containing the build files.
     * 
     * For webapps this is usually
     * something like /User/username/workspace/project/target/classes
     */
    @Parameter(property = "ninja.outputDirectory", defaultValue = "${project.build.outputDirectory}", required = true)
    private String buildOutputDirectory;
    
    /**
     * All directories to watch for changes.
     */
    @Parameter(property = "ninja.watchDirs", required = false)
    private File[] watchDirs;
    
    /**
     * Watch all directories on runtime classpath of project.  For single
     * maven projects this usually will be identical to the buildOutputDirectory and this
     * property is therefore redundant. However, for multi-module projects, all dependent
     * project build output directories will be included (assuming you launched
     * maven from parent project directory). A simple way to use this plugin and
     * watch for changes across an entire multi-module project.
     */
    @Parameter(property = "ninja.watchAllClassPathDirs", defaultValue = "false", required = true)
    private boolean watchAllClassPathDirs;
    
    /**
     * Watch all jars on runtime classpath of project. A simple way to monitor
     * all transitive dependencies and trigger a restart if they change.  Since
     * Java's filesystem monitoring API only watches directories, this property
     * adds the parent directory of where the jar is stored and also adds an
     * include rule to always match that jar filename.  If other files in the directory
     * of jars on your classpath (unlikely if you're using maven in a normal manner),
     * then just be cautious you'll want to add exclude rules to not include them.
     */
    @Parameter(property = "ninja.watchAllClassPathJars", defaultValue = "false", required = true)
    private boolean watchAllClassPathJars;
    
    /**
     * Includes in Java regex format. Negative regex patterns are difficult to
     * write, so include rules are processed before exclude rules.  Positive
     * matches shortcut the matching process and that file will be included.
     * Use "/" as path separator where needed.
     */
    @Parameter(property = "ninja.includes", required = false)
    protected List<String> includes;
    
    /**
     * Exludes in Java regex format. If you want to exclude all
     * freemarker templates use something like (.*)ftl.html$ for instance.
     * Use "/" as path separator where needed.
     */
    @Parameter(property = "ninja.excludes", required = false)
    private List<String> excludes;
    
    /**
     * Adds assets directory and freemarker templates to excluded files.
     * These are loaded from the src directory in dev mode.
     * 
     * Default excludes are: 
     * - "(.*)\.html$"
     * - "/assets/"
     */
    @Parameter(property = "ninja.useDefaultExcludes", defaultValue = "true", required = true)
    protected boolean useDefaultExcludes;
    
    /**
     * Context path for SuperDevMode.
     */
    @Parameter(property = "ninja.contextPath", required = false)
    private String contextPath;
    
    /**
     * Mode for SuperDevMode.
     */
    @Parameter(property = "ninja.mode", defaultValue=NinjaConstant.MODE_DEV, required = false)
    private String mode;

    /**
    * Port for SuperDevMode
    */
    @Parameter(property = "ninja.port", defaultValue="8080", required = false)
    private Integer port;
    
    /**
     * Amount of time to wait for file changes to settle down before triggering a
     * restart in SuperDevMode.
     */
    @Parameter(property = "ninja.settleDownMillis", defaultValue="500", required = false)
    private Long settleDownMillis;
    
    /**
     * Define the jvm arguments to use when starting jetty.
     */
    @Parameter(property = "ninja.jvmArgs", required = false)
    private List<String> jvmArgs;
    
    /**
     * Define the jvm arguments to use when starting jetty.
     * Use a space " " to separate arguments, unless preceded by a backslash "\".
     * Use this to specify jvm args from command line, as default maven parser
     * does not allow to escape default "," separator
     */
    @Parameter(property = "ninja.jvmArgsStr", required = false)
    private String jvmArgsStr;

    /**
     * Additionnal classpath to inject just after project classes.
     * Use "/" as path separator where needed.
     */
    @Parameter(property = "ninja.classpaths", required = false)
    private List<String> classpaths;
    

    @Override
    public void execute() throws MojoExecutionException {
        
        if (skip) {
            getLog().info("Skip flag is on. Will not execute.");
            return;
        }
        
        initMojoFromUserSubmittedParameters();
        

        List<String> classpathItems = new ArrayList<>();
        
        alertAndStopExecutionIfDirectoryWithCompiledClassesOfThisProjectDoesNotExist(
            buildOutputDirectory);
        
        classpathItems.add(buildOutputDirectory);
        
        for (String classpath : classpaths) {
            getLog().debug("classpath: "+classpath);
            classpathItems.add(classpath);
        }
        
        for (org.apache.maven.artifact.Artifact artifact: project.getArtifacts()) {
            getLog().debug("project-artifact: "+artifact.getFile().toString());
            classpathItems.add(artifact.getFile().toString());           
        }
        
        List<Artifact> allArtifactsFromNinjaStandaloneInPlugin 
            = getAllArtifactsComingFromNinjaStandalone(pluginArtifacts);
        
        for (Artifact artifact: allArtifactsFromNinjaStandaloneInPlugin) {
            
            //only add once...
            if (!classpathItems.contains(artifact.getFile().toString())) {
                getLog().debug("ninja-artifact: "+artifact.getFile().toString());
                classpathItems.add(artifact.getFile().toString());
            }
        
        }       
        
        Set<String> includesAsSet = new LinkedHashSet<>(includes);
        Set<String> excludesAsSet = new LinkedHashSet<>(excludes);
        
        // start building set of directories to recursively watch
        Set<Path> directoriesToRecursivelyWatch = new LinkedHashSet<>();
        
        // add only absolute paths so we can catch duplicates
        
        // add buildOutputDirectory
        directoriesToRecursivelyWatch.add(
            FileSystems.getDefault().getPath(
                buildOutputDirectory).toAbsolutePath());
        
        // add any watch directories - overrides existing
        if (this.watchDirs != null) {
            directoriesToRecursivelyWatch.clear();
            for (File watchDir: this.watchDirs) {
                directoriesToRecursivelyWatch.add(watchDir.toPath().toAbsolutePath());
            }
        }
        
        // add stuff from classpath
        for (org.apache.maven.artifact.Artifact artifact : project.getArtifacts()) {
            File file = artifact.getFile();
            
            if (file.isDirectory() && this.watchAllClassPathDirs) {
                directoriesToRecursivelyWatch.add(file.toPath().toAbsolutePath());
            }
            else if (file.getName().endsWith(".jar") && this.watchAllClassPathJars) {
                File parentDir = file.getParentFile();
                Path parentPath = parentDir.toPath().toAbsolutePath();
                
                // safe string for rules below (windows path must be escaped for
                // use in regular expressions
                String rulePrefix = parentDir.getAbsolutePath() + File.separator;
                rulePrefix = rulePrefix.replace("\\", "\\\\");
                
                // if not previously added then add an exclusion rule for everything in it
                if (!directoriesToRecursivelyWatch.contains(parentPath)) {
                
                    excludesAsSet.add(rulePrefix + "(.*)$");
                    
                }
                
                // we also need to add this jar with an inclusion rule so that we always match it
                includesAsSet.add(rulePrefix + file.getName() + "$");
                
                directoriesToRecursivelyWatch.add(parentPath);
            }       
        }
        
        getLog().info("------------------------------------------------------------------------");
        
        getLog().info("Ninja will watch dirs:");
        for (Path path : directoriesToRecursivelyWatch) {
            getLog().info(" " + path);
        }
        
        getLog().info("Ninja will launch with:");
        if (contextPath != null) {
            getLog().info(" context path: '" + contextPath + "'");
        } else {
             getLog().info(" root context");
        }
        getLog().info(" mode: " + mode);
        getLog().info(" port: " + port);
        if (jvmArgs != null) {
        	getLog().info(" jvm arguments: ");
        	for (String arg : jvmArgs) {
        	    getLog().info("  " + arg);
        	}
        }
        getLog().info("------------------------------------------------------------------------");
        

        
        try {
            //
            // build dependencies, start them, and then watch
            //
            RunClassInSeparateJvmMachine machine = new RunClassInSeparateJvmMachine(
                "NinjaJetty",
                NinjaMavenPluginConstants.NINJA_JETTY_CLASSNAME,
                classpathItems,
                buildJvmArguments(),
                project.getBasedir()
            );
            
            DelayedRestartTrigger restartTrigger = new DelayedRestartTrigger(machine);
            restartTrigger.setSettleDownMillis(settleDownMillis);
            
            restartTrigger.start();
            
            WatchAndRestartMachine watcher = new WatchAndRestartMachine(
                directoriesToRecursivelyWatch,
                includesAsSet,
                excludesAsSet,
                restartTrigger);
            
            // initial startup of machine
            machine.restart();
            
            watcher.run();
            
        } catch (IOException e) {
            getLog().error(e);
        }
    }
    
    private List<String> buildJvmArguments() {
        List<String> jvmArguments = new ArrayList<>();
        
        String systemPropertyDevMode 
                = "-D" + NinjaConstant.MODE_KEY_NAME + "=" + mode;
        
        jvmArguments.add(systemPropertyDevMode);

        String portSelection
                = "-D" + NinjaJetty.COMMAND_LINE_PARAMETER_NINJA_PORT + "=" + port;

        jvmArguments.add(portSelection);
        
        if (contextPath != null) {
            String systemPropertyContextPath = "-Dninja.context=" + contextPath;
            jvmArguments.add(systemPropertyContextPath);
        }
        
        jvmArguments.addAll(jvmArgs);
        
        return jvmArguments;
    }
    
    public static String[] splitUnescapedSpaces(String s) {
        String[] parts = s.replaceAll("^\\s+","").replaceAll("\\s+$","").split("(?<=(^|[^\\\\])(?>\\\\\\\\){0,1000}) +");
        for (int i=0; i<parts.length; i++)
        {
            parts[i] = parts[i].replace("\\\\", "\\").replaceAll("\\\\ +"," ");
        }
        return parts;
    }
    
    private void initMojoFromUserSubmittedParameters() {
        
        if (useDefaultExcludes) {
            
            excludes.addAll(
                Arrays.asList(
                    NinjaMavenPluginConstants.DEFAULT_EXCLUDE_PATTERNS));
            
        }
        
        // add supplied jvm args - overrides existing
        if (jvmArgsStr != null) {
            jvmArgs.clear();
            jvmArgs.addAll(Arrays.asList(splitUnescapedSpaces(jvmArgsStr)));
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
    private List<Artifact> getAllArtifactsComingFromNinjaStandalone(
        List<Artifact> artifacts) {
    
        List<Artifact> resultingArtifacts = new ArrayList<>();
        
        for (Artifact artifact: artifacts) {
        
            for (String dependencyTrail: artifact.getDependencyTrail()) {
            
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