FROM openjdk:8-jre-alpine
RUN mkdir -p /opt/processmining-baw/documents
RUN pwd
ADD target/CaseManagerAccelerator.jar docker-spring-boot.jar
ENV JAVA_OPTIONS '-Xmx400m'
EXPOSE 8080
CMD java -jar docker-spring-boot.jar
