# Distributed Maven Test Runner

## Overview

The purpose of this framework is to distribute and execute maven test jobs.  The assumptions are;
- the job to be run is a series of maven tests, executed via ```mvn test```
- that the tests can be broken down into simple groups (i.e. 100 Test java files can be run across 10 nodes evenly)
- that each of the worker nodes maps to a single VM
- that any success/failures of the jobs are sent back as simple logs to the source_ami

## Components

### Maven Distributor

- Java App with a simple HTTP API
- can be told to clone a git location, or a zipped source can be uploaded
- once a new run is triggered, the app will look for all files ending in Test and extract the names
- the app builds a list of each source project and their tests
- the app will then get the number of hosts registered with it
- buckets of tasks will be created (number of tests / number of hosts = _n_ buckets with a list of tests)
- the url for the source, along with the instructions, is then sent to each host
- hosts will respond with identifier information and run the mvn test command

### Maven Invoker

- Java App that works as a slave to the Distributor
- registers with the Distributor on startup via HTTP
- when it receives a job, examines the URL and retrieves the compressed source from the Distributor
- extracts the source
- runs ```mvn test``` with supplied parameters
- compresses and uploads the stdout to the Distributor
