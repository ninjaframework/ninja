# 9.3.15, 9.4
FROM jetty:9.3.15
RUN ["rm", "-rf", "/var/lib/jetty/webapps/ROOT*"]
COPY target/*.war /var/lib/jetty/webapps/ROOT.war