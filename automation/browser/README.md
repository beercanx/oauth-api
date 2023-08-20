# Browser Automation
* Test pack that does browser based testing using tools like Selenium

## How to run?
By default, the tests are disabled using tag `automation` because at the moment during CI there is nothing to test.

But to enable them is simple, set an environment variable of `ENABLE_AUTOMATION_BROWSER=true` and run `./gradlew test`

Or just use the saved and shared IntelliJ Idea run configuration `oauth-api:automation:browser [test]`