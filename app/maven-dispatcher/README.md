## API


### /api/host

GET /api/host
- returns a list of all hosts

POST /api/host
{
  "address":"[ipv4 address]",
  "port":"[port number]"
}
- adds the host to the repository

GET /api/host/file?fileName=[file name to download]
- retrieves the local file to be used by the invoker for execution


### /api/runner

GET /api/runner
- returns a list of job instances

GET /api/runner/[job instance id]
- returns details for the job instance

GET /api/runner/[job instance id]/log
- returns a zip file of the output for the job if there is one

POST /api/runner
{
  "git-url":"[url]",
  "username":"[username - optional]",
  "password":"[password - optional]"
}
- executes the job by cloning the passed git url and running

POST /api/runner/file
file=multipart zip file
- expects a zipped source directory
- unzips and executes
