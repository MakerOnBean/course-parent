FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./target/service-activity.jar server-gateway.jar
ENTRYPOINT ["java","-jar","/service-activity.jar", "&"]