# e

e is a zero-dependency micro library to model errors in Java/Scala applications. It aims to unify error models in an extensible, modular way.

## Table of Contents

1. [Installation](#installation)
2. [e-core](#e-core)
3. [e-scala](#e-scala)
4. [e-circe](#e-circe)
5. [e-play-json](#e-play-json)
6. [e-gson](#e-gson)
7. [Contributing](#contributing)
8. [License](#license)

## Installation

| Latest Version | Scala Version  |
| -------------- | -------------- |
| 0.2.1          | 2.13           |
| 0.2.1          | 2.12           |

e is published to Maven Central. In order to add it to your project, replace `version` and `scalaVersion` with correct versions and do following:

For Maven, add to your `pom.xml`

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
  
  <!-- Optional, for Scala projects with play-json -->
  <dependency>
    <groupId>dev.akif</groupId>
    <artifactId>e-play-json_{scalaVersion}</artifactId>
    <version>{version}</version>
  </dependency>
  
  <!-- Optional, for Java projects with gson -->
  <dependency>
    <groupId>dev.akif</groupId>
    <artifactId>e-gson</artifactId>
    <version>{version}</version>
  </dependency>
</dependencies>
```

For SBT, add to your `build.sbt`

```scala
libraryDependencies ++= Seq(
  "dev.akif"  % "e-core"      % "{version}",
  "dev.akif" %% "e-scala"     % "{version}", // Optional, for Scala projects
  "dev.akif" %% "e-circe"     % "{version}", // Optional, for Scala projects with circe
  "dev.akif" %% "e-play-json" % "{version}", // Optional, for Scala projects with play-json
  "dev.akif"  % "e-gson"      % "{version}"  // Optional, for Java projects with gson
)
```

For Gradle, add to your project's `build.gradle`

```javascript
dependencies {
  compile 'dev.akif:e-core:{version}'
  compile 'dev.akif:e-scala_{scalaVersion}:{version}'     // Optional, for Scala projects
  compile 'dev.akif:e-circe_{scalaVersion}:{version}'     // Optional, for Scala projects with circe
  compile 'dev.akif:e-play-json_{scalaVersion}:{version}' // Optional, for Scala projects with play-json
  compile 'dev.akif:e-gson:{version}'                     // Optional, for Java projects with gson
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

// E has various static constructor methods named `of`, this is the most complex one
E error3 = E.of(
  1,                                  // code
  "error-name-more-like-a-code",      // name
  "Human readable error message",     // message
  new Exception("causing-exception"), // cause
  data                                // data
);

// An example method that can fail with an error
public int divide(int i, int j) {
  if (j == 0) {
    // E is also an `Exception` so you can throw it
    // If you're into that kind of stuff
    throw E.of(1, "divide-by-zero", "Cannot divide by 0!");
  }
  
  return i / j;
}

// `toString()` of an `E` uses `DefaultEncoderE` to render the `E` as a Json String
// Will print
// {"code":1,"name":"error-name-more-like-a-code","message":"Human readable error message","cause":"causing-exception","data":{"key":"value"}}
System.out.println(error3);

// An `EncoderE` implementation for `String`
// Basically constructs a CSV of E's fields (except for `data` in this case)
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
            return E.of(code);
        } catch (Exception e) {
            // `dev.akif.e.DecodingFailure` is a runtime exception
            // Throw it to indicate a decoding failure when needed
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
// Brings in `Maybe` which is a type alias where the error type of `Either` is fixed to `E`
import dev.akif.e._

def divide(i: Int, j: Int): Maybe[Int] =
  if (j == 0) {
    // Constructing `E` is the same as Java, error is `Left` of Either
    Left(E.of(1, "divide-by-zero", "Cannot divide by 0!"))
  } else {
    // Value is `Right` of Either
    Right(i / j)
  }

// There are useful implicits extension methods for dealing with existing values.
import dev.akif.e.implicits._
import scala.util.{Failure, Success}

// `orE` method can be used to convert an `Option[A]` to `Maybe[A]`.

// Left({"code":1,"name":"empty"})
val maybe1: Maybe[String] = Option.empty[String].orE(E.of(1, "empty"))
// Right("foo")
val maybe2: Maybe[String] = Some("foo").orE(E.of(1, "empty"))

// `orE` method can be used to convert an `Either[L, R]` to `Maybe[R]` when an `L => E` is given.

// Left({"code":1,"name":"foo"})
val maybe3: Maybe[Int] = Left[String, Int]("foo").orE(left => E.of(1, left))
// Right(3)
val maybe4: Maybe[Int] = Right[String, Int](3).orE(_ => E.of(1, "bar"))

// `orE` method can be used to convert a `Try[A]` to `Maybe[A]` when a `Throwable => E` is given.

// Left({"cause":"foo"})
val maybe5: Maybe[Double] = Failure[Double](new Exception("foo")).orE(t => E.empty.cause(t))
// Right(3.14)
val maybe6: Maybe[Double] = Success[Double](3.14).orE(_ => E.empty)

// User doesn't have to know/use `Either` semantics to deal with `Maybe`.
import dev.akif.e.syntax._

// `maybe` methods can be used to convert any value or `E` to a `Maybe`.

val foo: String = "foo"
val e: E = E.of(1, "test", "Test")

val maybeFoo: Maybe[String]    = foo.maybe       // Lifts value into a `Maybe[String]`
val maybeInt: Maybe[Int]       = e.maybe[Int]    // Lifts E into a `Maybe[Int]`
val maybeString: Maybe[String] = e.maybe[String] // Lifts E into a `Maybe[String]`

// To check if a `Maybe` has an `E` or value

// false
println(maybeFoo.isError) // or `hasError`
// true
println(maybeInt.isValue) // or `hasValue`
```

## e-circe

`e-circe` depends on `e-scala` and [circe](https://circe.github.io/circe). It provides circe's `Encoder` and `Decoder` for `E` so that an `E` can be converted to/from circe's `Json`.

```scala
// Brings in circe's `Encoder` and `Decoder` instances for `E`
import dev.akif.e.circe._

import dev.akif.e.{E, Maybe}
import dev.akif.e.syntax._
import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax._

// `E` can be encoded and decoded

val e1: E = E.of(1, "test")

// {"code":1,"name":"test"}
val j1: Json = e1.asJson

// Right({"code":1,"name":"test"})
val e2: Decoder.Result[E] = j1.as[E]

// A `Maybe[A]` can be encoded as long as `A` can be encoded

val m1: Maybe[List[Int]] = List(1, 2, 3).maybe
val m2: Maybe[String] = E.of(1, "test").maybe[String]

// [1,2,3]
val j2: Json = m1.asJson

// {"code":1,"name":"test"}
val j3: Json = m2.asJson

// In case you don't want to use `Encoder`/`Decoder` of circe,
// there are instances of `EncoderE`/`DecoderE` of e itself for circe's `Json`

// {"code":1,"name":"test"}
val j4: Json = encoderEJson.encode(e1)

// {"code":1,"name":"test"}
val e3: E = decoderEJson.decodeOrThrow(j1)
// This will throw `DecodingFailure`
val e4: E = decoderEJson.decodeOrThrow(Json.arr(Json.fromString("foo")))

// There is also a safer `decode` method coming from `e-scala` as an extension.
import dev.akif.e._
val either: Either[DecodingFailure, E] = decoderEJson.decode(j1)

// Decoding error can be converted to an `E` during any Json decoding
// This is done by `decodeOrE` extension method.
// It makes decoding result a `Maybe`, which could be useful.

val j5: Json = Json.obj("foo" := "bar")

val maybeMap: Maybe[Map[String, String]] =
  j5.decodeOrE[Map[String, String]] { decodingFailure =>
    E.of(1, "decoding-failed", decodingFailure.message).cause(decodingFailure)
  }
```

## e-play-json

`e-play-json` depends on `e-scala` and [play-json](https://github.com/playframework/play-json). It provides Play Json's `Reads` and `Writes` for `E` so that an `E` can be converted to/from Play Json's `JsValue`.

```scala
// Brings in Play Json's `Reads` and `Writes` instances for `E`
import dev.akif.e.playjson._

import dev.akif.e.{E, Maybe}
import dev.akif.e.syntax._
import play.api.libs.json._

// `E` can be read and written as `JsValue`

val e1: E = E.of(1, "test")

// {"code":1,"name":"test"}
val j1: JsValue = Json.toJson(e1)

// Some({"code":1,"name":"test"})
val e2: Option[E] = j1.asOpt[E]

// A `Maybe[A]` can be written as `JsValue` as long as `A` can be written as `JsValue`

val m1: Maybe[List[Int]] = List(1, 2, 3).maybe
val m2: Maybe[String] = E.of(1, "test").maybe[String]

// [1,2,3]
val j2: JsValue = Json.toJson(m1)

// {"code":1,"name":"test"}
val j3: JsValue = Json.toJson(m2)

// In case you don't want to use `Reads`/`Writes` of Play Json,
// there are instances of `EncoderE`/`DecoderE` of e itself for Play Json's `JsValue`

// {"code":1,"name":"test"}
val j4: JsValue = encoderEJsValue.encode(e1)

// {"code":1,"name":"test"}
val e3: E = decoderEJsValue.decodeOrThrow(j1)
// This will throw `DecodingFailure`
val e4: E = decoderEJsValue.decodeOrThrow(Json.arr("foo"))

// There is also a safer `decode` method coming from `e-scala` as an extension.
import dev.akif.e._
val either: Either[DecodingFailure, E] = decoderEJsValue.decode(j1)

// Reading error (`JsError`) can be converted to an `E` during any JsValue reading
// This is done by `readOrE` extension method.
// It makes decoding result a `Maybe`, which could be useful.

val j5: JsValue = Json.obj("foo" -> "bar")

val maybeMap: Maybe[Map[String, String]] =
  j5.readOrE[Map[String, String]] { jsError =>
    E.of(1, "decoding-failed", jsError.toString)
  }
```

## e-gson

`e-gson` depends on `e-core` and [gson](https://github.com/google/gson). It provides Gson's `JsonSerializer` and `JsonDeserializer` for `E` so that an `E` can be converted to/from Json via Gson.

```java
import com.google.gson.*;
import dev.akif.e.*;
import dev.akif.e.gson.EGsonAdapter;

// This adapter is `JsonSerializer<E>`/`JsonDeserializer<E>` for gson as well as `EncoderE<JsonElement>`/`DecoderE<JsonElement>`
EGsonAdapter adapter = new EGsonAdapter();

// You will need to register `adapter` as a type adapter
Gson gson = new GsonBuilder()
    .registerTypeAdapter(E.class, adapter)
    .create();

E e1 = E.of(1, "test", "Test");

// To convert an `E` to Json String
// {"code":1,"name":"test","message":"Test"}
String json1 = gson.toJson(e1);

// To parse a Json String as `E`
E e2 = gson.fromJson(json1, E.class);

// Will fail with a `DecodingFailure` because Json is not a valid `E` Json
gson.fromJson("[1,2,3]", E.class);

e1.equals(e2); // Evaluates to `true`

// In case you want to use `EncoderE`/`DecoderE` directly

// {"code":1,"name":"test","message":"Test"}
JsonElement json2 = adapter.encode(e1);

E e3 = adapter.decodeOrThrow(json2);

e1.equals(e3); // Evaluates to `true`

JsonArray arr = new JsonArray();
arr.add("foo");

// Will fail with a `DecodingFailure` because `["foo"]` is not a valid `E` Json
E e4 = adapter.decodeOrThrow(arr);
```

## Contributing

All contributions are welcome. Please feel free to send a pull request. Thank you.

## License

e is licensed with MIT License. See [LICENSE.md](https://github.com/makiftutuncu/bookstore/blob/master/LICENSE.md) for details.
