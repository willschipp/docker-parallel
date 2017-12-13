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


{"git-url":"1c33afa7-d694-4090-ad30-9e1d83056295.zip","options":"-Dtest=-n \"Sale-Void a Finite integration method transaction\" -n \"Add Discount for merchant and perform transaction\" -n \"Create merchants with NetworkOnline gateway and perform test transactions for all integration types\" -n \"Auth - Full capture- Reversal, Finite integration type transaction\" -n \"Passbook - transaction data on Passbook page is correct\" -n \"Sale-Void a Finite integration method transaction\" -n \"Auth-Partial Cap-Void for a Dynamic transaction by using Query API \" -n \"Auth-Full Cap-Void for a Dynamic transaction by using Query API \" -n \"Auth-Full Cap-Void for a Finite transaction by using Query API \" -n \"Auth-FAR for a Finite transaction by using Query API \" -n \"Sale-Void a Finite transaction by using Query API\" -n \"ApplePay - Valid Transation - Check if a valid transaction passed\" -n \"AHD tnx - ECI Indicator is Fully Secure (merchant 3ds and enrolled card)\" -n \"MHF tnx - ECI Indicator is Partially Secure (merchant 3ds and NOT enrolled card)\" -n \"Successful execution test for merchant with Allow Tokenization = YES, CVV Mandate = NO\" -n \"Successful execution test for merchant with Allow Tokenization = YES, CVV Mandate = NO\" -n \"AHD tnx - Check if transaction passed with the MIN AMOUNT (enrolled card)\" -n \"AHD tnx - Check if transaction passed with the MAX AMOUNT (enrolled card)\" -n \"AHD tnx - Txn Type - Check if transaction is passed for SALE and AUTHORIZATION (Enrolled Cards)\" -Denv=niProdCopy -Dgateway=cyber -DisVDI=false -Dbrowser=chrome -DtestData=\"../neo-at-data\" -Dcucumber.options=\"--tags @happy_flow -n \"Sale-Void a Finite integration method transaction\" -n \"Add Discount for merchant and perform transaction\" -n \"Create merchants with NetworkOnline gateway and perform test transactions for all integration types\" -n \"Auth - Full capture- Reversal, Finite integration type transaction\" -n \"Passbook - transaction data on Passbook page is correct\" -n \"Sale-Void a Finite integration method transaction\" -n \"Auth-Partial Cap-Void for a Dynamic transaction by using Query API \" -n \"Auth-Full Cap-Void for a Dynamic transaction by using Query API \" -n \"Auth-Full Cap-Void for a Finite transaction by using Query API \" -n \"Auth-FAR for a Finite transaction by using Query API \" -n \"Sale-Void a Finite transaction by using Query API\" -n \"ApplePay - Valid Transation - Check if a valid transaction passed\" -n \"AHD tnx - ECI Indicator is Fully Secure (merchant 3ds and enrolled card)\" -n \"MHF tnx - ECI Indicator is Partially Secure (merchant 3ds and NOT enrolled card)\" -n \"Successful execution test for merchant with Allow Tokenization = YES, CVV Mandate = NO\" -n \"Successful execution test for merchant with Allow Tokenization = YES, CVV Mandate = NO\" -n \"AHD tnx - Check if transaction passed with the MIN AMOUNT (enrolled card)\" -n \"AHD tnx - Check if transaction passed with the MAX AMOUNT (enrolled card)\" -n \"AHD tnx - Txn Type - Check if transaction is passed for SALE and AUTHORIZATION (Enrolled Cards)\"\""}
