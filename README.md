# selenium grid docker

## Goal
- to leverage a docker container strategy to startup and manage a parallel selenium docker grid
- to leverage the deployment and standup of that grid from bamboo

## requirements
- packer
- virtualbox
- jdk8

## setup

- stand up 1 or more docker host using the packer script (base.json)
- import the resulting image
