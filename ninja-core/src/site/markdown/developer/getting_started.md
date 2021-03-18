Contribute
==========

**Great you want to contribute!**

It's really simple. Well. There are some rules that you should follow:

Documentation
-------------

The process is mostly automated and convenient.

- You'll find the documentation within `ninja-core/src/site/markdown`
- Edit the file from the github UI (https://github.com/ninjaframework/ninja/tree/develop/ninja-core/src/site/markdown)
- Add some explanations (footer section `Propose file change`)
- Press `Propose file change` button to create a PR (pull-request).


Code
----
- Make sure you are following the code style below.
- Make sure your feature is well tested.
- Make sure your feature is well documented (Javadoc).
- Make sure there is documentation for your feature at ninja-core/src/site/markdown.
- Make sure your feature runs inside ninja-integration-test
  or ninja-servlet-jpa-blog-integration-test if you wrote a feature related to JPA.
- Add your changes to changelog.md and your name to team.md.

Sign the [contributor agreement (electronically)](https://docs.google.com/forms/d/1Yasrxa17kYfaNDgbRvn77rM3WTnU_Um0rwz3GfzPp9g/viewform) and
send us a pull request to become a happy member of the Ninja family :)



### Code style

- **Don't use null**
-- This is not yet the case across the whole codebase.
-- For new code the rule of thumb is: Never use null. Consider using Optional instead.
-- More: https://github.com/google/guava/wiki/UsingAndAvoidingNullExplained
- **Make things immutable** (ImmutableMap etc)
-- This is not yet the case across the whole codebase.
-- New code should use immutability where possible.
-- Makes it easy to reason about state of variables and makes multithreading a breeze.
- **New tests should use AssertJ** (and no longer hamcrest or Junit equals).
-- AssertJ makes tests easier to read and understand + it gives better error messages
- Consider using the **builder pattern**
-- Constructors with many parameters are hard to read. A builder can help.
-- A builder has to work like MyNewClass.builder(). ... .build(). (Notice the static builder() - no "new" is needed).
- Default Sun Java / Eclipse code style (a default config for eclipse can be found at the project root eclipse-ninja-format.xml.
- If you change only tiny things only reformat stuff you actually changed. Otherwise reviewing is really hard.
- We use spaces / 4 spaces as a tab in all files (java, xml...).
- Files in git repo should have unix (LF) file endings. We are using the .gitattributes file to handle that for you.
- All files are UTF-8.


### Releasing

Making a Ninja release
 
1) Preparations

- Make sure you are using https://semver.org/ for versioning.
- Make sure changelog.md is updated
- Make sure upgrade-guide top version is updated
- Make sure the archetypes are up-to-date (Ninja version must match release version)
- Make sure the archetypes version in docu (JPA + getting_started) matches release version

2) Release to central maven repo

Make sure you got the credentials properly set up in your .m2/settings.xml:
<pre>
&lt;server&gt;
    &lt;id&gt;ossrh&lt;/id&gt;
    &lt;username&gt;sonatype username&lt;/username&gt;
    &lt;password&gt;sonatype password&lt;/password&gt;
&lt;/server&gt;
</pre>


- mvn release:clean
- mvn release:prepare
- mvn release:perform
- Log into https://oss.sonatype.org and release the packages

3) Publish website

Make sure you got the credentials properly set up in your .m2/settings.xml:

<pre>
&lt;server&gt;
    &lt;id&gt;github-project-site&lt;/id&gt;
    &lt;username&gt;git&lt;/username&gt;
&lt;/server&gt;
</pre>

- git checkout TAG
- cd ninja-core
- mvn site site:deploy

And back to develop:

- git checkout develop
