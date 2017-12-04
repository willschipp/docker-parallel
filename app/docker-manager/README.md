# docker manager app

## overview

- responsible for the coordination of build and running of remote executions
- requires the setting of target docker hosts
- requires remote docker hosts to have repository access
- requires remote docker hosts to have remote API enabled

## process

- set a target host
- request a "build"
  - send json fragment with git-url and target image
- request a "run"
