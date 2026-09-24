FROM eclipse-temurin:21-jdk

WORKDIR /usr/local/app

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

CMD ["./mvnw", "spring-boot:run"]