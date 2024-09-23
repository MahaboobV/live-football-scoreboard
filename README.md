# Live Football World Cup Scoreboard Library

## Description
A simple Java-based library that allows you to track live football matches, update scores, and display ongoing match summaries in real time. Built with a focus on clean code, TDD, and SOLID principles.

## Table of Contents
- [Installation](#installation)
- [Usage](#usage)
- [Running Tests](#running-tests)

###Installation

### Clone the Repository
 To get a copy of the project on your local machine, run:

```bash
git clone https://github.com/MahaboobV/live-football-scoreboard.git

```

### Build the project 

Navigate into the project directory:

```bash
cd LiveFootballScoreboard
```
Build the project using your preferred build tool:

- For Maven

```bash
mvn clean install
```
- For gradle
 
 ```bash
gradle build
```

### Run the project

For Console Application:

- For Maven:
 ```bash
mvn exec:java -Dexec.mainClass="com.example.football.scoreboard.LiveFootballScoreboardApp"
```
- For Gradle
 ```bash
gradle run
```

### Run the tests
1. **Unit Tests**: Run unit tests to verify functionality.

- Using Maven 

 ```bash
mvn test
```
- Using Gradle

 ```bash
gradle test
```

2. **Test Coverage** : Generate test coverage reports to ensure code quality.

To ensure the quality of the codebase, used **JaCoCo** for generating test coverage reports. Below are the steps to generate and view the coverage report: 


####Steps to Generate Test Coverage:

1. ***Run the tests***:

   Execute the following Maven command to run the tests:

 ```bash
 mvn clean test
  ```

2. ***Generate the JaCoCo coverage report***:

   After the tests are run, you can generate the test coverage report by executing:

```bash
  mvn jacoco:report

```

3. ***View the report***:
   The coverage report will be generated in the following location:

```bash
  target/site/jacoco/index.html

```
   Open this file in a web browser to view the detailed coverage statistics.