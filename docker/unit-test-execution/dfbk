FROM base

COPY . /usr/src/app

WORKDIR /usr/src/app

ENV tests -Dtest=*

ENTRYPOINT /usr/local/maven/bin/mvn clean test $tests
