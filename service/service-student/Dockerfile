FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./target/service-student.jar service-student.jar
ENTRYPOINT ["java","-jar","/service-student.jar", "&"]
EXPOSE 8201