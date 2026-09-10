FROM eclipse-temurin:21-jdk

WORKDIR /usr/local/app

COPY . .

RUN ./mvnw clean package -DskipTests

ENTRYPOINT ["java","-jar","target/system-0.0.1-SNAPSHOT.jar"]