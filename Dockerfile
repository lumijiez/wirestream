FROM maven:3.9.9-amazoncorretto-23 AS builder

WORKDIR /app

COPY . /app

RUN mvn clean package -DskipTests

FROM openjdk:23-jdk

WORKDIR /app

COPY --from=builder /app/target/wirestream-1.0.0.jar wirestream-1.0.0.jar

ENTRYPOINT ["java", "-jar", "wirestream-1.0.0.jar"]
