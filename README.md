[![Build Status](https://travis-ci.org/testinfected/cli.png?branch=master)](https://travis-ci.org/testinfected/cli)


### Getting Started
Build yourself (for latest version), or simply download from Maven Central:

```xml
<dependency>
      <groupId>com.vtence.cli</groupId>
      <artifactId>cli</artifactId>
      <version>1.1-SNAPSHOT</version>
</dependency>
```

```java
CLI cli = new CLI() {{
    name("petstore"); version("1.0");
    description("A web application built without frameworks");

    option("env", "-e", "--environment ENV", "Specifies the server environment").defaultingTo("development");
    option("port", "-p", "--port PORT", "Runs the server on this port").asType(int.class).defaultingTo(8080);
    option("timeout", "--timeout SECONDS", "Sets session expiration time").asType(int.class).defaultingTo(4500);
    option("encoding", "--encoding ENCODING", "Specifies the server output encoding").defaultingTo("UTF-8");
    option("quiet", "-q", "--quiet", "Sets the server to operate quietly").defaultingTo(false);
    
    operand("webroot", "webroot", "Location of the web application").ofType(File.class);
    
    ending("use --help to show this help message");
}};

// Assuming command line:
// petstore -p 8088 -e production --timeout 9000 /path/to/webapp/root 
Args args = cli.parse("-p", "8088", "-e", "production", "--timeout", "9000", "/path/to/webapp/root")

int port = args.get("port");
String env = args.get("env");
int timeout = args.get("timeout");
boolean quiet = args.has("quiet");

File webroot = args.get("webroot");
```

### Want to know more?

Checkout out examples in the [acceptance tests](https://github.com/testinfected/cli/blob/master/src/test/java/com/vtence/cli/CLIUsageTest.java)
