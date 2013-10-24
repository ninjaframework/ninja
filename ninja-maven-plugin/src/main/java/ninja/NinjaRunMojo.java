package ninja;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

import com.google.common.collect.Lists;

/**
 * Finds all artifacts by names in the current project.
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
    protected MavenProject project;

    /**
     * The entry point to Aether, i.e. the component doing all the work.
     * 
     * @component
     */
    private RepositorySystem repoSystem;

    /**
     * The current repository/network configuration of Maven.
     * 
     * @parameter default-value="${repositorySystemSession}"
     * @readonly
     */
    private RepositorySystemSession repoSession;

    /**
     * The project's remote repositories to use for the resolution of project
     * dependencies.
     * 
     * @parameter default-value="${project.remoteProjectRepositories}"
     * @readonly
     */
    private List<RemoteRepository> projectRepos;

    /**
     * The project's remote repositories to use for the resolution of plugins
     * and their dependencies.
     * 
     * @parameter default-value="${project.remotePluginRepositories}"
     * @readonly
     */
    private List<RemoteRepository> pluginRepos;

    @Override
    public void execute() throws MojoExecutionException {

        System.out
                .println("------------------------------------------------------------");
        //System.out.println(ResolveTransitiveDependencies.class.getSimpleName());
        
        List<String> classpathItems = Lists.newArrayList();
        classpathItems.add(".");
        
        classpathItems.add("/Users/ra/bibliothek/coden/workspace_t35/ninja/ninja-maven-plugin/integration-test/target/classes");
        
        for (org.apache.maven.artifact.Artifact artifact : project.getArtifacts()) {
            System.out.println("artifact : " + artifact.getArtifactId());
            System.out.println("artifact file : " + artifact.getFile());
            classpathItems.add(artifact.getFile().toString());           
        }
        
        
        Path path = FileSystems.getDefault().getPath("/Users/ra/bibliothek/coden/workspace_t35/ninja/ninja-maven-plugin/integration-test/target/classes");
        
        try {
            NWatchAndTerminate nWatchAndTerminate = new NWatchAndTerminate(
                    path,
                    true, 
                    "ninja.standalone.NinjaJetty", 
                    classpathItems);
            nWatchAndTerminate.processEvents();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
 
     

//        RepositorySystem system = repoSystem;
//
//        RepositorySystemSession session = newRepositorySystemSession(system);
//        
//        Artifact artifact = new DefaultArtifact(
//                String.format("%s:%s:%s", project.getGroupId(), project.getArtifactId(), project.getVersion()));
//
//        //RemoteRepository repo = newCentralRepository();
//
//        DependencyFilter classpathFlter = DependencyFilterUtils
//                .classpathFilter(JavaScopes.COMPILE);
//
//        CollectRequest collectRequest = new CollectRequest();
//        collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));
//        
//        for (RemoteRepository remoteRepository : projectRepos) {
//            collectRequest.addRepository(remoteRepository);
//        }
//        
//
//        DependencyRequest dependencyRequest = new DependencyRequest(
//                collectRequest, classpathFlter);
//
//        List<ArtifactResult> artifactResults;
//        try {
//            artifactResults = system.resolveDependencies(
//                    session, dependencyRequest).getArtifactResults();
//            
//            for (ArtifactResult artifactResult : artifactResults) {
//                System.out.println(artifactResult.getArtifact() + " resolved to "
//                        + artifactResult.getArtifact().getFile());
//            }
//            
//        } catch (DependencyResolutionException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }



    }

//    public static RepositorySystem newRepositorySystem() {
//        return org.eclipse.aether.examples.manual.ManualRepositorySystemFactory
//                .newRepositorySystem();
//        // return
//        // org.eclipse.aether.examples.guice.GuiceRepositorySystemFactory.newRepositorySystem();
//        // return
//        // org.eclipse.aether.examples.plexus.PlexusRepositorySystemFactory.newRepositorySystem();
//    }

    public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils
                .newSession();

        LocalRepository localRepo = new LocalRepository("target/local-repo");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(
                session, localRepo));

        //session.setTransferListener(new ConsoleTransferListener());
        //session.setRepositoryListener(new ConsoleRepositoryListener());

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

        return session;
    }

//    public static RemoteRepository newCentralRepository() {
//        return new RemoteRepository.Builder("central", "default",
//                "http://repo1.maven.org/maven2/").build();
//    }

    // public List<Artifact> resolve(Artifact root,
    // String scope) throws DependencyResolutionException {
    // final DependencyFilter filter =
    // DependencyFilterUtils.classpathFilter(scope);
    // if (filter == null) {
    // throw new IllegalStateException(
    // String.format("failed to create a filter for '%s'", scope)
    // );
    // }
    // return this.resolve(root, scope, filter);
    // }
    //
    // public List<Artifact> resolve( final Artifact root,
    // final String scope, final DependencyFilter filter)
    // throws DependencyResolutionException {
    // final Dependency rdep = new Dependency(root, scope);
    // final CollectRequest crq = this.request(rdep);
    // final List<Artifact> deps = new LinkedList<Artifact>();
    // deps.addAll(
    // this.fetch(
    // this.session(),
    // new DependencyRequest(crq, filter)
    // )
    // );
    // return deps;
    // }
    //
    //
    // private CollectRequest request(final Dependency root) {
    // final CollectRequest request = new CollectRequest();
    // request.setRoot(root);
    // for (RemoteRepository repo : this.remotes) {
    // if (!repo.getProtocol().matches("https?|file|s3")) {
    // Logger.warn(
    // this,
    // "%s ignored (only S3, HTTP/S, and FILE are supported)",
    // repo
    // );
    // continue;
    // }
    // request.addRepository(repo);
    // }
    // return request;
    // }
    //
    //
    // /**
    // * Create RepositorySystemSession.
    // * @return The session
    // */
    // private RepositorySystemSession session() {
    // final LocalRepository local = new LocalRepository(this.localRepo);
    // final MavenRepositorySystemSession session =
    // new MavenRepositorySystemSession();
    // session.setLocalRepositoryManager(
    // repoSession.newLocalRepositoryManager(local)
    // );
    // session.setTransferListener(new LogTransferListener());
    // return session;
    // }
    //
    //
    // private List<Artifact> fetch(final RepositorySystemSession session,
    // final DependencyRequest dreq) throws DependencyResolutionException {
    // final List<Artifact> deps = new LinkedList<Artifact>();
    // try {
    // Collection<ArtifactResult> results;
    // synchronized (this.localRepo) {
    // results = this.system
    // .resolveDependencies(session, dreq)
    // .getArtifactResults();
    // }
    // for (ArtifactResult res : results) {
    // deps.add(res.getArtifact());
    // }
    // // @checkstyle IllegalCatch (1 line)
    // } catch (Exception ex) {
    // throw new DependencyResolutionException(
    // new DependencyResult(dreq),
    // new IllegalArgumentException(
    // Logger.format(
    // "failed to load '%s' from %[list]s into %s",
    // dreq.getCollectRequest().getRoot(),
    // Aether.reps(dreq.getCollectRequest().getRepositories()),
    // session.getLocalRepositoryManager()
    // .getRepository()
    // .getBasedir()
    // ),
    // ex
    // )
    // );
    // }
    // return deps;
    // }
    //

}