Multi module Maven project deployment
-------------------------------------
Deployment of multi module Maven project requires an additional step due to the limitation of Maven Assembly Plugin as described in http://maven.apache.org/plugins/maven-assembly-plugin/faq.html#module-binaries

In essence,

> "In a multimodule hierarchy, when a child module declares the parent POM in its section, Maven interprets this to mean that the parent project's build must be completed before the child build can start. This ensures that the parent project is in its final form by the time the child needs access to its POM information. In cases where the Assembly Plugin is included as part of that parent project's build process, it will execute along with everything else as part of the parent build - before the child build can start. If the assembly descriptor used in that parent build references module binaries, it effectively expects the child build to be completed before the assembly is processed. This leads to a recursive dependency situation, where the child build depends on the parent build to complete before it can start, while the parent build depends on the presence of child-module artifacts to complete successfully. Since these artifacts are missing, the Assembly Plugin will complain about missing artifacts, and the build will fail."

So we need to create an additional distribution module just for the purpose of creating all-in-one jar.

Create a Maven module with an arbitrary name say 'distribution'. The only file you need in that module is a pom.xml that has a dependency defined to all modules.

```xml
<modelVersion>4.0.0</modelVersion>
<artifactId>distribution</artifactId>
<packaging>pom</packaging>
<name>Distribution</name>
<parent>
    <groupId>com.company</groupId>
    <artifactId>project-parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>
<dependencies>
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>proj-module-1</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>proj-module-2</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>2.5.2</version>
            <configuration>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
                <archive>
                    <manifest>
                        <mainClass>ninja.standalone.NinjaJetty</mainClass>
                    </manifest>
                </archive>
            </configuration>
        </plugin>
    </plugins>
</build>    
```

Now `cd` to distribution and fire `mvn assembly:single`. This will generate an MY-APPLICATION-jar-with-dependencies.jar in the target dir.
Now you can follow the instruction in the "Ninja standalone" page.
