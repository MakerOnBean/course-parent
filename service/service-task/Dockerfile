FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./target/service-task.jar server-gateway.jar
ENTRYPOINT ["java","-jar","/service-task.jar", "&"]