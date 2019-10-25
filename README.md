# e

e is a zero-dependency micro library to model errors in Java/Scala applications. It aims to unify error models in an extensible, modular way.

## Table of Contents

1. [Installation](#installation)
2. [e-core](#e-core)
3. [e-scala](#e-scala)
4. [e-circe](#e-circe)
5. [Contributing](#contributing)
6. [License](#license)

## Installation

| Latest Version | Scala Version  |
| -------------- | -------------- |
| 0.1.4          | 2.13<br />2.12 |

e **will be** published to Maven Central. In order to add it to your project, replace `version` and `scalaVersion` with correct versions and do following:

For Maven

```xml
<dependencies>
  <dependency>
    <groupId>dev.akif</groupId>
    <artifactId>e-core</artifactId>
    <version>{version}</version>
  </dependency>
  
  <!-- Optional, for Scala projects -->
  <dependency>
    <groupId>dev.akif</groupId>
    <artifactId>e-scala_{scalaVersion}</artifactId>
    <version>{version}</version>
  </dependency>
  
  <!-- Optional, for Scala projects with circe -->
  <dependency>
    <groupId>dev.akif</groupId>
    <artifactId>e-circe_{scalaVersion}</artifactId>
    <version>{version}</version>
  </dependency>
</dependencies>
```

For SBT

```scala
libraryDependencies ++= Seq(
  "dev.akif"  % "e-core"  % "{version}",

  // Optional, for Scala projects
  "dev.akif" %% "e-scala" % "{version}",

  // Optional, for Scala projects with circe
  "dev.akif" %% "e-circe" % "{version}"
)
```

For Gradle

```javascript
dependencies {
  compile 'dev.akif:e-core:{version}'
  
  // Optional, for Scala projects
  compile 'dev.akif:e-scala_{scalaVersion}:{version}'
  
  // Optional, for Scala projects with circe
  compile 'dev.akif:e-circe_{scalaVersion}:{version}'
}
```

## e-core

`e-core` is the core module of e which only contains main types such as

* `E` the error type itself
* `EncoderE` type to convert an `E` to a given type
* `DecoderE` type to convert a given type to `E`

It is a Java library and has no dependency. Therefore, it should be able to be used in pretty much anywhere that supports Java.

Here's an overview of e's capabilities:

```java
// All classes of e-core are here.
import dev.akif.e.*;

// An empty error
E error1 = E.empty;

// Another error having some error code
// It's a new instance, a copy of `error1`
// There are also `name`, `message`, `cause` and `data` methods
E error2 = error1.code(404);

// E can contain a map of related data
Map<String, String> data = new HashMap<>();
data.put("key", "value");

// E has various constructors, this is the most complex one
E error3 = new E(
  1,
  "error-name-more-like-a-code",
  "Human readable error message",
  new Exception("causing-exception"),
  data
);

// An example method that can fail with an error
public int divide(int i, int j) {
  if (j == 0) {
    // E is also an `Exception` so you can throw it
    // If you're into that kind of stuff
    throw new E(1, "divide-by-zero", "Cannot divide by 0!");
  }
  
  return i / j;
}

// `toString()` of an `E` uses `DefaultEncoderE` to render the `E` as a Json String
// Will print
// {"code":1,"name":"error-name-more-like-a-code","message":"Human readable error message","cause":"causing-exception","data":{"key":"value"}}
System.out.println(error3);

// An `EncoderE` implementation for `String`
// Basically constructs a CSV of E's fields (except for `data`)
EncoderE<String> csvEncoder = new EncoderE<> {
    @Override public String encode(E e) {
        return String.format(
            "%s,%s,%s,%s",
            e.hasCode()    ? "" : e.code,
            e.hasName()    ? "" : e.name,
            e.hasMessage() ? "" : e.message,
            e.hasCause()   ? "" : e.cause.getMessage()
        );
    }
}

// Will print
// 1,error-name-more-like-a-code,Human readable error message,causing-exception
String encoded = csvEncoder.encode(error3);
System.out.println(encoded);

// A `DecoderE` implementation for `String`
// Constructs an `E` from given `String` but only cares about `code`
DecoderE<String> codeExtractingDecoder = new DecoderE<String> {
    @Override public E decodeOrThrow(String s) {
        try {
            String[] parts = s.split(",");
            String codeString = parts[0];
            int code = Integer.parseInt(codeString);
            return E.empty.code(code);
        } catch (Exception e) {
            // For demo purposes, don't really do catch and throw in your code
            throw new DecodingFailure("Cannot extract code from " + s, e);
        }
    }
}

// Will print
// 1
E error4 = codeExtractingDecoder.decode(encoded);
System.out.println(error4.code);

// Will fail because there is no code (int) in "foo"
codeExtractingDecoder.decode("foo");
```

## e-scala

`e-scala` depends on `e-core`. It provides some implicits for e as well as a type alias `Maybe`.

```scala
// TODO
```

## e-circe

`e-circe` depends on `e-scala` and [circe](https://circe.github.io/circe). It provides circe's `Encoder` and `Decoder` for `E` so that an `E` can be converted to/from circe's `Json`.

```scala
// TODO
```

## Contributing

All contributions are welcome. Please feel free to send a pull request. Thank you.

## License

e is licensed with MIT License. See [LICENSE.md](https://github.com/makiftutuncu/bookstore/blob/master/LICENSE.md) for details.