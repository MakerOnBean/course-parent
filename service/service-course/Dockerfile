FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./target/service-course.jar service-course.jar
ENTRYPOINT ["java","-jar","/service-course.jar", "&"]
EXPOSE 8202