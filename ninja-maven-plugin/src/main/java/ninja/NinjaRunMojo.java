package ninja;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;

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
    
    @Override
    public void execute() throws MojoExecutionException {
        
        // Read property from system properties.
        String contextPathProperty = System.getProperty("ninja.context", "/");
        // If not set up in pom.xml then use system property.
        if (contextPath == null) {
            contextPath = contextPathProperty;
        }
        
        getLog().debug(
                "Directory for classes is (used to start local jetty and watch for changes: " 
                + buildOutputDirectory);
        
        getLog().info("------------------------------------------------------------------------");
        if (getLog().isInfoEnabled()) {
            getLog().info("Launching Ninja SuperDevMode with '" + contextPath + "' context path.");
        }
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
                    contextPath);
            
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