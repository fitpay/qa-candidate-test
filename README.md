# FitPay QA Candidate Test Project
The intent of this project is to provide the constructs of a QA automation project which faciliates automated
integration testing leveraging junit and SoapUI.  From this project QA candidates will have the foundations to expand upon as part of a short *homework* assignment.  

The project provides two different forms of executing essentially the same integration test cases.  The test cases
are by no means complete, they are only intended as a form of guidence.

## What's being tested?
The integration tests are testing a simple restful [tax calculation api]( https://anypoint.mulesoft.com/apiplatform/fitpay/#/portals/apis/12167/versions/12577/pages/13380).  The api provides a restful resource (http://qa.fit-pay.com/tax) which will calculate base sales tax for Colorado and California presenting the results with embedded [HAL](http://stateless.co/hal_specification.html) based hypermedia links.

Information on possible responses along with detailed samples is provided in the *API Reference* section of the [portal](https://anypoint.mulesoft.com/apiplatform/fitpay/#/portals/apis/12167/versions/12577/pages/13380).

## Prerequisites
* Maven 3.2.3
* Java >= 1.7
```
java version "1.7.0_67"
Java(TM) SE Runtime Environment (build 1.7.0_67-b01)
Java HotSpot(TM) 64-Bit Server VM (build 24.65-b04, mixed mode)
```
* SoapUI >= 5.0.0 if editing the SoapUI project

### Executing the Tests

##### Junit
```
mvn integration-test -P junit-integration-test
```
##### SoapUI
```
mvn integration-test -P soapui-integration-test
```

### Extending the Tests

##### Junit
Refer to the [SampleIT.java](https://github.com/fitpay/qa-candidate-test/blob/master/src/test/java/fitpay/tests/SampleIT.java) 
implementation for a few sample tests.  Add your test cases and modifications to this class or leverage the pattern in a new *your_test_caseIT.java* class.

##### SoapUI
Refer to the [soapui-project.xml](https://github.com/fitpay/qa-candidate-test/blob/master/src/test/resources/soapui-project.xml) SoapUI project file.  Add your test cases and modifications to this project or leverage the pattern in a new SoapUI project file.

##### I don't like this technologies, can't I use my own?
Yes! If you prefer a different form of test case implementation over junit and/or SoapUI, the world is your oyster... implement away so long as the below requirements are met.   Just add your own maven profile to the [pom.xml](https://github.com/fitpay/qa-candidate-test/blob/master/pom.xml).

## Requirements
* A pull request is issued with your changes
* Instructions for executing your tests if not utilizing the junit or SoapUI implementations

## Homework

This project is not intended to involve a huge investment of time, only to establish the patterns you think about when testing a component like this.  It does not need to be a 110% implemented project, only limited to key patterns in the following areas to help us understand how you approach a QA/automation challenge.

Fork this project and tackle the following challenges:

### Assignment #1 - Test Portability
You might notice the testing endpoint is hard coded for the tax api, this does not fit well in cloud based environment where a test case must be reuseable across all environments, developer systems, QA environments, production environments, etc...

Can you provide an easy data driven mechanism to make the test cases portable?

### Assignment #2 - Data Driven Testing
The tests utilize a static set of data, making the implementations not very scalable in terms of adding additional test cases.

Can you provide for a more data driven approach to the test cases?

### Assignment #3 - Test Coverage
Testing coverage is rather limited to happy path testing.

Can you provide a more comprehensive approach derived from reading the API specification for the tax service?

### Brownie Points - A Random Defect

**Coming Soon!**

