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
    
    public final String NINJA_JETTY_CLASS_AND_CLASSPATH = "ninja.standalone.NinjaJetty";
    
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject mavenProject;
    
    /** 
     * @parameter default-value="${plugin.artifacts}" 
     */
    private List<Artifact> pluginArtifacts;

    @Override
    public void execute() throws MojoExecutionException {
        
        String completeClassesDir = new File("").getAbsolutePath() + CLASSES_DIRECTORY;

        System.out
                .println("------------------------------------------------------------");
        
        List<String> classpathItems = Lists.newArrayList();
        //classpathItems.add(".");

        classpathItems.add(completeClassesDir);
        
        //Artifact artifact = Artifact
        
        for (org.apache.maven.artifact.Artifact artifact : pluginArtifacts) {
            //classpathItems.add(artifact.getFile().toString());  
        } 
        
        for (org.apache.maven.artifact.Artifact artifact : mavenProject.getArtifacts()) {
            classpathItems.add(artifact.getFile().toString());           
        }       
        
        Path path = FileSystems.getDefault().getPath(completeClassesDir);
        
        try {
            NWatchAndTerminate nWatchAndTerminate = new NWatchAndTerminate(
                    path,
                    true, 
                    NINJA_JETTY_CLASS_AND_CLASSPATH, 
                    classpathItems);
            nWatchAndTerminate.processEvents();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}