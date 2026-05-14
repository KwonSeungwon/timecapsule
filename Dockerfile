FROM eclipse-temurin:17-jdk-focal
ARG JAR_FILE=build/libs/*.war
COPY ${JAR_FILE} app.war
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.war"]
EXPOSE 3000
