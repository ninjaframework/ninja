package ninja;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.common.collect.Lists;
import java.util.Arrays;

/**
 * Starts Ninja's hot relaod dev mode.
 * 
 * @goal run
 * @phase test
 * @threadSafe
 * @requiresDependencyResolution compile
 */
public class NinjaRunMojo extends AbstractMojo {
    

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject mavenProject;
    
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
     * @parameter default-value="true"
     */
    protected boolean useDefaultExcludes;
    

    @Override
    public void execute() throws MojoExecutionException {
        

        initMojoFromUserSubmittedParameters();
        
        
        List<String> classpathItems = Lists.newArrayList();
        
        
        String directoryWithCompiledClassesOfThisProject 
                = System.getProperty(NinjaMavenPluginConstants.USER_DIR) 
                + NinjaMavenPluginConstants.DEFAULT_CLASSES_DIRECTORY;
        
        classpathItems.add(directoryWithCompiledClassesOfThisProject);

        for (org.apache.maven.artifact.Artifact artifact : mavenProject.getArtifacts()) {
            classpathItems.add(artifact.getFile().toString());           
        }       
        
        Path path = FileSystems.getDefault().getPath(directoryWithCompiledClassesOfThisProject);
        
        try {
            
            WatchAndRestartNinjaMachine nWatchAndTerminate = new WatchAndRestartNinjaMachine(
                    path,
                    classpathItems,
                    excludesAsList);
            
            nWatchAndTerminate.processEvents();
            
        } catch (IOException e) {
            getLog().error(e);
        }
    }
    
    
    void initMojoFromUserSubmittedParameters() {
    
        if (excludes != null && excludes.length > 0) {
            excludesAsList.addAll(Arrays.asList(excludes));
            
        }
        
        
        if (useDefaultExcludes) {
        
            excludesAsList.addAll(
                    Arrays.asList(
                        NinjaMavenPluginConstants.DEFAULT_EXCLUDE_PATTERNS));
            
        }
    
    }

}