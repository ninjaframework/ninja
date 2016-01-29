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
  or ninja-servlet-jpa-blog-integrationt-est if you wrote a feature related to JPA.
- Add your changes to changelog.md and your name to team.md.

Sign the [contributor agreement (electronically)](https://docs.google.com/forms/d/1Yasrxa17kYfaNDgbRvn77rM3WTnU_Um0rwz3GfzPp9g/viewform) and
send us a pull request to become a happy member of the Ninja family :)



### Code style

- Default Sun Java / Eclipse code style (a default config for eclipse can be found at the project root eclipse-ninja-format.xml.
- If you change only tiny things only reformat stuff you actually changed. Otherwise reviewing is really hard.
- We use spaces / 4 spaces as a tab in all files (java, xml...).
- Files in git repo should have unix (LF) file endings. We are using the .gitattributes file to handle that for you.
- All files are UTF-8.


### Releasing

Making a Ninja release
 
1) Preparations

- Make sure you are using http://semver.org/ for versioning.
- Make sure changelog.md is updated
- Make sure upgrade-guide top version is updated
- Make sure the archetypes are up-to-date (Ninja version must match release version)
- Make sure the archetypes version in docu (JPA + getting_started) matches release version

2) Release to central maven repo

- mvn release:clean
- mvn release:prepare
- mvn release:perform
- Log into http://oss.sonatype.org and release the packages

3) Publish website

- git checkout TAG
- cd ninja-core
- mvn site site:deploy

And back to develop:

- git checkout develop
