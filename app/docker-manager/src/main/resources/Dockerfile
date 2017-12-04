FROM maven:3.5.2-jdk-8

WORKDIR /usr/src/app

ENV MAVEN_OPTS=-Dmaven.repo.local=../m2repo/
ENV test -Dtest=*

RUN git clone https://github.com/willschipp/docker-maven-cache.git

RUN mv /usr/src/app/docker-maven-cache/pom.xml /usr/src/app/pom.xml

RUN mvn -B -e -C -T 1C org.apache.maven.plugins:maven-dependency-plugin:3.0.2:go-offline

RUN git clone ${url}

RUN mv /usr/src/app/${work_dir}/* /usr/src/app/.

ENTRYPOINT mvn clean test $test
