# smoke test

## prerequisites

- running remote server with docker host and remote api exposed
- access to public repositories

## start

- run the java app ```java -jar docker-manager-0.0.1-SNAPSHOT.jar```
- setup a host ```curl -XPOST http://localhost:8080/data/hosts -d @host.json```
- start a build ```curl -XPOST http://localhost:8080/api/build -d @build.json```
- once a build is complete [TBD] start instances ``` curl -XPOST http://localhost:8080/api/run?name=new-image&url=https://github.com/willschipp/sample-app.git&instances=3```
- check for the containers ```curl -XGET http://localhost:8080/data/containers```
- choose a container to monitor ```curl -XGET http://localhost:8080/api/run?id=[container id]``` or ```curl -XGET http://localhost:8080/data/containers/[id]```