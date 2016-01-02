To generate the code coverage report, execute the following command:

mvn clean verify

This will generate code coverage report in each of the modules. In order to view the same, open the following file in your browser.

target/site/cobertura/index.html

Please note that the above folder is created under each of the modules. For example:

ninja-servlet/target/site/cobertura/index.html
busniess-delegate/target/site/cobertura/index.html