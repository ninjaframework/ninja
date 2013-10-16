package ninja;

import java.io.File;
import java.util.Collection;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystemSession;

import com.jcabi.aether.Classpath;


/**
 * Finds all artifacts by names in the current project.
 *
 * @goal run
 * @phase test
 * @threadSafe
 */
public class NinjaRunMojo extends AbstractMojo {
    /**
     * Maven project, to be injected by Maven itself.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private transient MavenProject project;

    /**
     * The current repository/network configuration of Maven.
     * @parameter default-value="${repositorySystemSession}"
     * @readonly
     */
    private transient RepositorySystemSession session;
    
    
    @Override
    public void execute() {
      Collection<File> jars = new Classpath(
        project,
        this.session.getLocalRepository().getBasedir(),
        "test"
      );
      
      for (File file : jars) {
          
          System.out.println("file: " + file.getAbsolutePath());
          
      }
    }

}