# Airport Summary Tool

## Usage
1. `cd build/libs`
2. `java -jar backgroundback-1.0-SNAPSHOT-all.jar`
3. Input one or more ICAO identifiers at any time.

## Description
This exercise accepts one or more ICAO identifiers and outputs JSON airport summaries.

Gradle is used with a combination of open source libraries including OkHttp, Lombok, and Jackson.

Requests are asynchronously sent to the Airport and Weather Conditions API's.

Code was written to easily allow re-use with different invocations, such as being run as a web backend. A CLI interface was simplest and easiest for the purposes of this exercise.

JUnit tests were written for various transformations. In a production environment these would be expanded, including functional/integration tests.

## Development Time Spent
An estimated 7 hours were spent developing this application.

## Additional Helpful Gradle Tasks
* `shadowJar` must be used to create new jars
* `test` runs JUnit tests

## Ideas for Improving Implementation
While the command-line interface is nice, the application would become more "production ready" by placing it behind a web server with an API. This would look similar to the Weather Conditions and Airport API's, such as https://qa.something.com/airports/summary/<airport_identifier>.

Caching could be implemented to prevent repeated calls to the Airport and Weather Conditions API's. These could expire after a set amount of time. This way, if there were X requests per minute, there would only be one API call to each airport instead of potentially X.

The command-line interface could be improved by showing a loading indicator. This would allow users to know how many more summaries are in progress.

The summary could be expanded to include more airport information and weather information. This would take advantage of how it acts as a single endpoint for weather and airport information about a single airport (versus two calls to the weather conditions and airport). Additional fields could be added to help pilots, such as information about services available at airports like courtesy cars.
