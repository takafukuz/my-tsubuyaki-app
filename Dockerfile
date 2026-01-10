FROM tomcat:10.1-jdk21-temurin

# デフォルトのROOTアプリを削除
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# 作成したWARをコピー
COPY target/my-tsubuyaki-app.war /usr/local/tomcat/webapps/ROOT.war

# Redisson の jar を Tomcat の lib にコピー
COPY docker/redisson*.jar /usr/local/tomcat/lib/

# redisson.yaml を Tomcat に配置
COPY docker/redisson.yaml /usr/local/tomcat/redisson.yaml

# context.xml を Tomcat の conf に上書き
COPY docker/context.xml /usr/local/tomcat/conf/context.xml



EXPOSE 8080

ENV CATALINA_OPTS="-Denv=prod"

CMD ["catalina.sh", "run"]