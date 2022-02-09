# Testing

To run tests, you can use the Maven lifecycle `test`:

```sh
mvn test integration-test
```

This will run all tests in the repository. If you only want to do unit tests, remove
`integration-test`. Due to the Maven lifecycle, integration tests will generally cause unit tests to
run regardless.

## Integration Test Requirements

Integration tests require a local Docker instance to run (a Postgres instance is spun up there).

All integration tests should go within the `test/.../integration/` folder and extend `BaseApiTest`.
For optimization, if a test is designed to not use the database or be idempotent, give it a `@Tag`
with the proper `DatabaseUsage` value; this prevents the database from being re-initialized (slow!)
every time a test completes. Failed tests will still cause a rebuild (as they may not have removed
all traces of themselves).

## Surefire Reports

Before generating a report of all the test results, you must run:

```sh
mvn site -DgenerateReports=false
```

This will generate all of the CSS and images that accompany the report; without them, the report is
quite bland and hard to read.

Then, generate the report:

```sh
mvn verify
```

## Coverage

In order to view coverage reports in a human readable format, use `verify` (or
`post-integration-test`):

```sh
mvn verify
```

Then, `target/site/jacoco/index.html` will provide a nice interface showing the coverage status.

If you prefer more proactive coverage reports, there are a number of plugins for IDEs which will
report this, such as
[Coverage Gutters](https://marketplace.visualstudio.com/items?itemName=ryanluker.vscode-coverage-gutters)
for Visual Studio Code.

## Specific Tests

If you only want to run certain test classes, use the `-Dtest=SomeClassTest` flag on Maven. You can
also run only certain methods themselves with `-Dtest=SomeClassTest#testSomeMethod`.

Please note, this is likely to produce strange (or entirely wrong) results from JaCoCo (for code
coverage).

## Skipping Testing

Some tests can be annoyingly slow for certain development build processes (particularly when
debugging the build process itself). To skip tests, add `-DskipTests` to the command line.

Please note, this is likely to produce strange (or entirely wrong) results from JaCoCo (for code
coverage).
