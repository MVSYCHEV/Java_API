FROM maven:3.6.3-openjdk-14
WORKDIR /test
COPY . .
CMD mvn clean test