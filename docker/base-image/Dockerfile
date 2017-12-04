FROM maven:3.5.2-jdk-8

RUN git clone https://github.com/willschipp/sample-app.git

WORKDIR ./sample-app

ENV test -Dtest=*

ENTRYPOINT mvn clean test $test
