Tests
======

Testing the effectiveness of our agent has proven to be much more difficult than we have anticipated.

### Analyzing Performance

While we were able to easily run and debug games against multiple non-dummy agents. However, the game has high levels of randomness - making it hard to determine manually the effectiveness of a specific modification/feature, and the performance of our agent in general.

We tried to streamline the process, but could not come up with a solution that will be worth its implementation time - the log parser turned out to be less friendly than expected, and other solutions required more refined control on simulation progress.

In the end we had to settle, and proceed with slower and less effective manual runs and analysis.


### Correctness and Errors

Due to the complex nature of the game environment end-to-end tests were done manually.  

However, we used unit tests to ensure each function is behaving as expected, minimize the possible errors, and provide additional level of validation to our logic. This proved to be very useful, as we found many off-by-one issues, and had to reason and rethink any tested code.  
The Java ecosystem enabled us to easily add any test we needed (Guice for dependency injection, and JUnit, Mockito, AssertJ for the tests themselves).  

#### Build, CI and Coverage

Hosting the project on Github allowed us to setup many complex integrations with no significant effort.  
In particular, for every commit all tests are running automatically on the build server, which provides info about failing tests or test coverage. This serves to assist and encourage proper testing of the agent, for example by having push notification whenever a build fails or code coverage significantly changed (FYI, the services in use are TravisCI, CodeCov and Slack).
