/**
 * Copyright (C) 2012-2019 the original author or authors.
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

import ninja.build.WatchAndRestartMachine;
import ninja.build.DelayedRestartTrigger;
import ninja.build.RunClassInSeparateJvmMachine;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import ninja.build.ArgumentTokenizer;

import ninja.standalone.NinjaJetty;
import ninja.standalone.AutoStandalone;
import ninja.standalone.Standalone;
import ninja.utils.NinjaConstant;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

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
    protected List<org.apache.maven.artifact.Artifact> pluginArtifacts;

    /**
     * Skip execution of this plugin.
     */
    @Parameter(property = "ninja.skip", defaultValue="false", required = true)
    protected boolean skip;
    
    /**
     * Directory containing the build files.
     * 
     * For webapps this is usually
     * something like /User/username/workspace/project/target/classes
     */
    @Parameter(property = "ninja.outputDirectory", defaultValue = "${project.build.outputDirectory}", required = true)
    protected String buildOutputDirectory;
    
    /**
     * All directories to watch for changes.
     */
    @Parameter(property = "ninja.watchDirs", required = false)
    protected File[] watchDirs;
    
    /**
     * Watch all directories on runtime classpath of project.  For single
     * maven projects this usually will be identical to the buildOutputDirectory and this
     * property is therefore redundant. However, for multi-module projects, all dependent
     * project build output directories will be included (assuming you launched
     * maven from parent project directory). A simple way to use this plugin and
     * watch for changes across an entire multi-module project.
     */
    @Parameter(property = "ninja.watchAllClassPathDirs", defaultValue = "false", required = true)
    protected boolean watchAllClassPathDirs;
    
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
    protected boolean watchAllClassPathJars;
    
    /**
     * Includes in Java regex format. Negative regex patterns are difficult to
     * write, so include rules are processed before exclude rules.  Positive
     * matches shortcut the matching process and that file will be included.
     */
    @Parameter(property = "ninja.includes", required = false)
    protected List<String> includes;
    
    /**
     * Exludes in Java regex format. If you want to exclude all
     * freemarker templates use something like (.*)ftl.html$ for instance.
     */
    @Parameter(property = "ninja.excludes", required = false)
    protected List<String> excludes;
    
    /**
     * Adds assets directory and freemarker templates to excluded files.
     * These are loaded from the src directory in dev mode.
     * 
     * Default excludes are: 
     * - "(.*)ftl\\.html$"
     * - "File.pathSeparator + "assets" + File.separator"
     */
    @Parameter(property = "ninja.useDefaultExcludes", defaultValue = "true", required = true)
    protected boolean useDefaultExcludes;
    
    /**
     * Context path for SuperDevMode.
     */
    @Parameter(property = "ninja.context", required = false)
    protected String context;
    
    @Deprecated
    protected String contextPath;
    
    /**
     * Mode for SuperDevMode.
     */
    @Parameter(property = "ninja.mode", defaultValue=NinjaConstant.MODE_DEV, required = false)
    protected String mode;

    /**
     * Port for SuperDevMode (can also be set in conf/application.conf)
     */
    @Parameter(property = "ninja.port", required = false)
    protected Integer port;
    
    /**
     * SSL port for SuperDevMode (can also be set in conf/application.conf)
     */
    @Parameter(property = "ninja.ssl.port", required = false)
    protected Integer sslPort;
    
    /**
     * Main class to run in SuperDevMode. Defaults to default standalone class
     * in Ninja.
     */
    @Parameter(property = "ninja.mainClass", required = false)
    protected String mainClass;
    
    /**
     * Extra arguments to pass to the forked JVM. If you keep your arguments
     * fairly simple, they should be tokenized (split) correctly.  Uses excellent
     * DrJava ArgumentTokenizer class for splitting line into separate arguments.
     */
    @Parameter(property = "ninja.jvmArgs", required = false)
    protected String jvmArgs;
    
    /**
     * Amount of time to wait for file changes to settle down before triggering a
     * restart in SuperDevMode.
     */
    @Parameter(property = "ninja.settleDownMillis", defaultValue="500", required = false)
    protected Long settleDownMillis;

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

        for (org.apache.maven.artifact.Artifact artifact : project.getArtifacts()) {
            classpathItems.add(artifact.getFile().toString());           
        }
        
        List<Artifact> allArtifactsFromNinjaStandaloneInPlugin 
            = getAllArtifactsComingFromNinjaStandalone(pluginArtifacts);
        
        for (Artifact artifact: allArtifactsFromNinjaStandaloneInPlugin) {
            
            //only add once...
            if (!classpathItems.contains(artifact.getFile().toString())) {
                classpathItems.add(artifact.getFile().toString());
            }
        
        }       
        
        Set<String> includesAsSet = new LinkedHashSet<>((includes != null ? includes : Collections.EMPTY_LIST));
        Set<String> excludesAsSet = new LinkedHashSet<>((excludes != null ? excludes : Collections.EMPTY_LIST));
        
        // start building set of directories to recursively watch
        Set<Path> directoriesToRecursivelyWatch = new LinkedHashSet<>();
        
        // add only absolute paths so we can catch duplicates
        
        // add buildOutputDirectory
        directoriesToRecursivelyWatch.add(
            FileSystems.getDefault().getPath(
                buildOutputDirectory).toAbsolutePath());
        
        // add any watch directories
        if (this.watchDirs != null) {
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
        
        // which standalone (works with -Dninja.standalone to maven OR in pom.xml)
        String mainClassToRun = (mainClass != null ? mainClass : AutoStandalone.class.getCanonicalName());
        
        getLog().info("------------------------------------------------------------------------");
        
        getLog().info("Ninja will watch dirs:");
        for (Path path : directoriesToRecursivelyWatch) {
            getLog().info(" " + path);
        }
        getLog().info("");
        getLog().info("Ninja will launch with:");
        getLog().info("      context: " + (getContextPath() != null ? getContextPath() : "<default>"));
        getLog().info("         mode: " + mode);
        getLog().info("         port: " + (port != null ? port : "<default>"));
        getLog().info("     ssl port: " + (sslPort != null ? sslPort : "<default>"));
        getLog().info("    mainClass: " + mainClassToRun);
        getLog().info("extra jvmArgs: " + (jvmArgs != null ? jvmArgs : "<none>"));
        getLog().info("------------------------------------------------------------------------");
        
        try {
            //
            // build dependencies, start them, and then watch
            //
            
            RunClassInSeparateJvmMachine machine = buildRunClassInSeparateJvmMachine(
                "Standalone",
                mainClassToRun,
                classpathItems,
                buildJvmArguments(),
                project.getBasedir()
            );
            
            DelayedRestartTrigger restartTrigger = buildDelayedRestartTrigger(machine);
            restartTrigger.setSettleDownMillis(settleDownMillis);
            
            restartTrigger.start();
            
            WatchAndRestartMachine watcher = buildWatchAndRestartMachine(
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
    
    protected String getContextPath() {
        if (this.context != null) {
            return this.context;
        } else {
            return this.contextPath;
        }
    }
    
    // so we can mock a fake one for unit testing
    protected DelayedRestartTrigger buildDelayedRestartTrigger(RunClassInSeparateJvmMachine machine) {
        return new DelayedRestartTrigger(machine);
    }
    
    protected WatchAndRestartMachine buildWatchAndRestartMachine(
            Set<Path> directoriesToRecursivelyWatch,
            Set<String> includes,
            Set<String> excludes,
            DelayedRestartTrigger restartTrigger) throws IOException {
        return new WatchAndRestartMachine(
                directoriesToRecursivelyWatch,
                includes,
                excludes,
                restartTrigger);
    }
    
    // so we can mock a fake one for unit testing
    protected RunClassInSeparateJvmMachine buildRunClassInSeparateJvmMachine(
            String name,
            String classNameWithMainToRun,
            List<String> classpath, 
            List<String> jvmArguments,
            File mavenBaseDir) {
        return new RunClassInSeparateJvmMachine(
            name,
            classNameWithMainToRun,
            classpath,
            buildJvmArguments(),
            mavenBaseDir
        );
    }
    
    protected List<String> buildJvmArguments() {
        List<String> jvmArguments = new ArrayList<>();
        
        String systemPropertyDevMode 
                = "-D" + NinjaConstant.MODE_KEY_NAME + "=" + mode;
        
        jvmArguments.add(systemPropertyDevMode);

        if (port != null) {
            String portSelection
                    = "-D" + Standalone.KEY_NINJA_PORT + "=" + port;
            jvmArguments.add(portSelection);
        }
        
        if (sslPort != null) {
            String sslPortSelection
                    = "-D" + Standalone.KEY_NINJA_SSL_PORT + "=" + sslPort;
            jvmArguments.add(sslPortSelection);
        }
        
        if (getContextPath() != null) {
            String contextPathSelection
                    = "-D" + Standalone.KEY_NINJA_CONTEXT_PATH + "=" + getContextPath();
            jvmArguments.add(contextPathSelection);
        }
        
        if (jvmArgs != null) {
            // use excellent DrJava library to tokenize arguments (do not keep tokens)
            List<String> tokenizedArgs = ArgumentTokenizer.tokenize(jvmArgs, false);
            getLog().debug("JVM arguments tokenizer results:");
            for (String s : tokenizedArgs) {
                getLog().debug("argument: " + s + "");
            }
            jvmArguments.addAll(tokenizedArgs);
        }
        
        return jvmArguments;
    }
    
    protected void initMojoFromUserSubmittedParameters() {
        
        if (useDefaultExcludes) {
            
            excludes.addAll(
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
    protected List<Artifact> getAllArtifactsComingFromNinjaStandalone(
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
    
    protected void alertAndStopExecutionIfDirectoryWithCompiledClassesOfThisProjectDoesNotExist(
        String directoryWithCompiledClassesOfThisProject) throws MojoExecutionException {
        
        File classesDir = null;

        if (directoryWithCompiledClassesOfThisProject != null) {
            classesDir = new File(directoryWithCompiledClassesOfThisProject);
        }
        
        if (classesDir == null || !classesDir.exists()) {
            
            getLog().error("Directory with classes does not exist: " + directoryWithCompiledClassesOfThisProject);
            getLog().error("Maybe running 'mvn compile'  before running 'mvn ninja:run' helps :)");
            
            // BAM!
            //System.exit(1);
            throw new MojoExecutionException("Directory with classes does not exist: " + directoryWithCompiledClassesOfThisProject);
        }
    
    }
}