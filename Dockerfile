FROM openjdk:17-jdk

WORKDIR /app

COPY app/target/app-1.0-SNAPSHOT.jar /app/app-1.0-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "app-1.0-SNAPSHOT.jar"]