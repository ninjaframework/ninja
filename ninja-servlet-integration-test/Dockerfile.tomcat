# 7.0.81-jre8, 8.0.46-jre8, 8.5.20-jre8, 9.0.0
FROM tomcat:9.0.0.M26
RUN ["rm", "-rf", "/usr/local/tomcat/webapps/ROOT"]
ADD target/*.war /usr/local/tomcat/webapps/ROOT.war