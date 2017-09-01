# 10.1.0.Final
FROM jboss/wildfly:10.1.0.Final
RUN ["rm", "-rf", "/opt/jboss/wildfly/standalone/deployments/ROOT*"]
COPY target/*.war /opt/jboss/wildfly/standalone/deployments/ROOT.war