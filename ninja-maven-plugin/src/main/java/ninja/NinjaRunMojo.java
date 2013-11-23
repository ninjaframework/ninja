package ninja;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;

import com.google.common.collect.Lists;

/**
 * Starts Ninja's hot relaod dev mode.
 * 
 * @goal run
 * @phase test
 * @threadSafe
 * @requiresDependencyResolution compile
 */
public class NinjaRunMojo extends AbstractMojo {
    
    public final String CLASSES_DIRECTORY = File.separator + "target" + File.separator + "classes";
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException {
        
        List<String> classpathItems = Lists.newArrayList();
        
        
        
        String directoryWithCompiledClassesOfThisProject 
                = System.getProperty(NinjaMavenPluginConstants.USER_DIR) + CLASSES_DIRECTORY;
        
        classpathItems.add(directoryWithCompiledClassesOfThisProject);

        for (org.apache.maven.artifact.Artifact artifact : mavenProject.getArtifacts()) {
            classpathItems.add(artifact.getFile().toString());           
        }       
        
        Path path = FileSystems.getDefault().getPath(directoryWithCompiledClassesOfThisProject);
        
        try {
            
            WatchAndRestartNinjaMachine nWatchAndTerminate = new WatchAndRestartNinjaMachine(
                    path,
                    classpathItems);
            nWatchAndTerminate.processEvents();
            
        } catch (IOException e) {
            getLog().error(e);
        }
    }

}