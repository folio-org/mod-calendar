# Testing

To run tests, you can use the Maven lifecycle `test`:

```sh
mvn test
```

This will run all tests in the repository.

## Coverage

In order to view coverage reports in a human readable format, use `jacoco:report`:

```sh
mvn jacoco:report
```

Then, `target/site/jacoco/index.html` will provide a nice interface showing the coverage status.

If you prefer more proactive coverage reports, there are a number of plugins for IDEs which will
report this, such as
[Coverage Gutters](https://marketplace.visualstudio.com/items?itemName=ryanluker.vscode-coverage-gutters)
for Visual Studio Code.

## Specific Tests

If you only want to run certain test classes, use the `-Dtest=SomeClassTest` flag on Maven. You can
also run only certain methods themselves with `-Dtest=SomeClassTest#testSomeMethod`.

## Skipping Testing

The unit tests can be annoyingly slow for certain development build processes. To skip them, add
`-Dmaven.test.skip=true` to the command line.
