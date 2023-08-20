# API Automation
Test pack that does direct API based testing using tools like Rest Assured.

## How to run?
By default, the tests are disabled using tag `automation` because at the moment during CI there is nothing to test.

But to enable them is simple, set an environment variable of `ENABLE_AUTOMATION_API=true` and run `./gradlew test`

Or just use the saved and shared IntelliJ Idea run configuration `oauth-api:automation:api [test]`