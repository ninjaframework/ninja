Multi module Maven project deployment
-------------------------------------
Deployment of multi module Maven project requires an additional step due to the limitation of Maven Assembly Plugin as described in http://maven.apache.org/plugins/maven-assembly-plugin/faq.html#module-binaries

In essence,

> "In a multimodule hierarchy, when a child module declares the parent POM in its section, Maven interprets this to mean that the parent project's build must be completed before the child build can start. This ensures that the parent project is in its final form by the time the child needs access to its POM information. In cases where the Assembly Plugin is included as part of that parent project's build process, it will execute along with everything else as part of the parent build - before the child build can start. If the assembly descriptor used in that parent build references module binaries, it effectively expects the child build to be completed before the assembly is processed. This leads to a recursive dependency situation, where the child build depends on the parent build to complete before it can start, while the parent build depends on the presence of child-module artifacts to complete successfully. Since these artifacts are missing, the Assembly Plugin will complain about missing artifacts, and the build will fail."

So we need to create an additional distribution module just for the purpose of creating all-in-one jar.

Create a Maven module with an arbitrary name say 'distribution'. The only file you need in that module is a pom.xml that has a dependency defined to all modules.

<pre class="prettyprint">
&lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
&lt;artifactId&gt;distribution&lt;/artifactId&gt;
&lt;packaging&gt;pom&lt;/packaging&gt;
&lt;name&gt;Distribution&lt;/name&gt;
&lt;parent&gt;
    &lt;groupId&gt;com.company&lt;/groupId&gt;
    &lt;artifactId&gt;project-parent&lt;/artifactId&gt;
    &lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
    &lt;relativePath&gt;../pom.xml&lt;/relativePath&gt;
&lt;/parent&gt;
&lt;dependencies&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;com.company&lt;/groupId&gt;
        &lt;artifactId&gt;proj-module-1&lt;/artifactId&gt;
        &lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
        &lt;groupId&gt;com.company&lt;/groupId&gt;
        &lt;artifactId&gt;proj-module-2&lt;/artifactId&gt;
        &lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
    &lt;/dependency&gt;
&lt;/dependencies&gt;
&lt;build&gt;
    &lt;plugins&gt;
        &lt;plugin&gt;
            &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
            &lt;artifactId&gt;maven-assembly-plugin&lt;/artifactId&gt;
            &lt;version&gt;2.5.2&lt;/version&gt;
            &lt;configuration&gt;
                &lt;descriptorRefs&gt;
                    &lt;descriptorRef&gt;jar-with-dependencies&lt;/descriptorRef&gt;
                &lt;/descriptorRefs&gt;
                &lt;archive&gt;
                    &lt;manifest&gt;
                        &lt;mainClass&gt;ninja.standalone.NinjaJetty&lt;/mainClass&gt;
                    &lt;/manifest&gt;
                &lt;/archive&gt;
            &lt;/configuration&gt;
        &lt;/plugin&gt;
    &lt;/plugins&gt;
&lt;/build&gt;
</pre>

Now `cd` to distribution and fire `mvn assembly:single`. This will generate an MY-APPLICATION-jar-with-dependencies.jar in the target dir.
Now you can follow the instruction in the "Ninja standalone" page.
