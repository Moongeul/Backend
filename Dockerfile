# Dockerfile

# jdk17 Image Start
FROM openjdk:17

ARG JAR_FILE=build/libs/book-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} book_Backend.jar
ENTRYPOINT ["java","-jar","book_Backend.jar"]