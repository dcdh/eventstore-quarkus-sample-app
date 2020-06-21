# todo-app

### Intellij IDEA testing
> Setup Junit testing

To run test we need to define the environment variable `TESTCONTAINERS_RYUK_DISABLED` to `true`.

To do this :
1. go to `Run > Edit Configurations...`
1. remove all defined tests in `JUnit`
1. go to `Templates > JUnit`
1. in `Environment variables:` add `TESTCONTAINERS_RYUK_DISABLED=true`
