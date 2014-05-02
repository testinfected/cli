[![Build Status](https://travis-ci.org/testinfected/cli.png?branch=master)](https://travis-ci.org/testinfected/cli)
[![Coverage Status](https://coveralls.io/repos/testinfected/cli/badge.png)](https://coveralls.io/r/testinfected/cli)

### Getting Started
Build yourself (for latest version) using Gradle or Maven, or simply download from Maven Central:

```xml
<dependency>
      <groupId>com.vtence.cli</groupId>
      <artifactId>cli</artifactId>
      <version>1.1</version>
</dependency>
```

First let's define a command line:

```java
CLI cli = new CLI() {{
    name("petstore"); version("1.0");
    description("A web application built without frameworks");

    option("-e", "--environment ENV", "Environment to use for configuration (default: development)").defaultingTo("development");
    option("-h", "--host HOST", "Host address to bind to (default: 0.0.0.0)").defaultingTo("0.0.0.0");
    option("-p", "--port PORT", "Port to listen on (default: 8080)").ofType(int.class).defaultingTo(8080);
    option("--timeout SECONDS", "Session timeout in seconds (default: 15 min)").ofType(int.class).defaultingTo(900);
    flag("-q", "--quiet", "Operate quietly");
    flag("-h", "--help", "Print this help message");

    operand("webroot", "Location of the web application").ofType(File.class);
    
    epilog("use --help to show this help message");
}};
```

Now assuming program is started with:

```-p 8088 -e production --timeout 9000 /path/to/webapp/root```

Here's how we would parse the arguments:

```java
cli.parse(args); // Typical program args

String env = cli.get("-e");
String host = cli.get("-h");
int port = cli.get("-p");
int timeout = cli.get("--timeout");
boolean quiet = cli.has("-q");

File webroot = cli.get("webroot");
```

### Want to know more?

Checkout out [usage examples](https://github.com/testinfected/cli/blob/master/src/test/java/com/vtence/cli/CLIUsageTest.java)
