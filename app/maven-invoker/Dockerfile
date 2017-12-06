FROM maven:3.5.2-jdk-8

COPY ./target/maven-invoker-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java","-Dmaven.home=/usr/share/maven","-jar","maven-invoker-0.0.1-SNAPSHOT.jar"]
