FROM openjdk:21-jdk
WORKDIR /app
COPY ./target/*-exec.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
