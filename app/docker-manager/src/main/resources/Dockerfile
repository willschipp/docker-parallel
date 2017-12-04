FROM maven:3.5.2-jdk-8

RUN git clone ${url}

WORKDIR ./sample-app

ENV test -Dtest=*

ENTRYPOINT mvn clean test $test
