# Income Tax View and Change Microservice

This is the protected backend for the Quarterly Reporting Service (MTD ITSA). 

Frontend: https://github.com/hmrc/income-tax-view-change-frontend
Stub: https://github.com/hmrc/income-tax-view-change-dynamic-stub

### Run the application


To run all income-view-and-change services via **service-manager-2**:

```
sm2 --start ITVC_BACKEND_ALL -r
```


### To run the application locally execute the following:

```
./run.sh
```

or

```
sbt 'run 9082'
```

### Test the application

To test the application execute

```
./run_all_tests.sh
```

or

```
sbt clean compile coverage Test/test it/test coverageOff coverageReport
```


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")

