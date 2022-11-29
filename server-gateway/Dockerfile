FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./target/server-gateway.jar server-gateway.jar
ENTRYPOINT ["java","-jar","/server-gateway.jar", "&"]
EXPOSE 80