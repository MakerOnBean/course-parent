FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./service-student.jar service-student.jar
ENTRYPOINT ["java","-jar","/service-student.jar", "&"]
EXPOSE 8201