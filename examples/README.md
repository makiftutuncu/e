# Example Projects and Use Cases for e

You can find some example projects featuring e under this directory.

| Project                                          | Link                     |
| ------------------------------------------------ | ------------------------ |
| Spring Boot project written in Java              | [Link](e-spring-example) |
| Play Framework project written in Scala with ZIO | [Link](e-play-example)   |
| http4s project written in Scala                  | [Link](e-http4s-example) |
| Ktor project written in Kotlin                   | [Link](e-ktor-example)   |

Here are some general uses cases of e that are also implemented in these example projects.

## 1. Eliminating the Need for Exceptions

Exceptions are dangerous and costly. Using them as a means of representing errors is not ideal. To avoid it, one could treat errors as values like any other data in the code. Then we can pass errors around but this is not always trivial.

Here's a good and long discussion on errors as values vs. exceptions: [https://softwareengineering.stackexchange.com/questions/405038](https://softwareengineering.stackexchange.com/questions/405038)

e provides `E` type for treating errors as data and `EOr` type for wrapping other values that can potentially fail. They both have friendly APIs and by using them, you can leave the exceptions to actual exceptional cases.

Here is an example method throwing exceptions:

```java
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public <A> A one(List<A> list) {
    if (list == null) {
        throw new IllegalArgumentException("List is null");
    }
  
    if (list.isEmpty()) {
        throw new IllegalArgumentException("List is empty");
    }
  
    if (list.size() != 1) {
        throw new IllegalArgumentException("List has " + list.size() + " items");
    }
    
    return list.get(0);
}

// Boom
String s = one<String>(null);
// Boom
int i = one<Integer>(new ArrayList<>());
// Boom
long l = one<Long>(Arrays.asList(1L, 2L));
// "hello"
String one = one<String>(Arrays.asList("hello"));
```

Here is the same example without the exceptions:

```java
import e.java.E;
import e.java.EOr;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public <A> EOr<A> one(List<A> list) {
    if (list == null) {
        return E.fromName("invalid-argument").message("List is null").toEOr();
    }
  
    if (list.isEmpty()) {
        return E.fromName("invalid-argument").message("List is empty").toEOr();
    }
  
    if (list.size() != 1) {
        return E.fromName("invalid-argument")
            .message("List has more than 1 items")
            .data("size", list.size())
            .toEOr();
    }
    
    return EOr.from(list.get(0));
}

EOr<String> s = one<String>(null);
// hasError: true
// hasValue: false
// {"name":"invalid-argument","message":"List is null"}

EOr<Integer> i = one<Integer>(new ArrayList<>());
// hasError: true
// hasValue: false
// {"name":"invalid-argument","message":"List is empty"}

EOr<Long> l = one<Long>(Arrays.asList(1L, 2L));
// hasError: true
// hasValue: false
// {"name":"invalid-argument","message":"List has more than 1 items","data":{"size":"2"}}

EOr<String> one = one<String>(Arrays.asList("hello"));
// hasError: false
// hasValue: true
// "hello"
```

You can see that using e, we can eliminate the need for exceptions and write safer, more expressive code. All this happen in compile time, so no runtime surprises either.

## 2. Using `code` of an `E` as HTTP Status Code

HTTP defines many status codes (for example https://tools.ietf.org/html/rfc2324#section-2.3.2). When serving HTTP requests from a backend application, we use different codes for different cases.

`E` type contains an integer `code` field that can be useful for mapping an error to an HTTP status code. You can create an error with a code and pass it around. When you reach the edge of your application where you will build an HTTP response, you can use the `code` field to decide which HTTP status to use.

Here's an example in Play Framework:

```scala
import e.scala.E
import e.scala.EOr
import play.api.mvc.Results.Status
import play.api.mvc.{Result, Results}
import scala.concurrent.Future

def respond(eor: EOr[String], codeIfSuccess: Status = Results.Ok): Future[Result] =
  eor.fold(
    e     => Future.successful(Status(e.code.getOrElse(500))(e.toString)),
    value => Future.successful(codeIfSuccess(value))
  )

def first(list: List[String]): EOr[String] =
  EOr.fromOption(
    list.headOption,
    E.name("empty").code(400)
  )

respond(first(List.empty))
// 400 Bad Request
// {"code":400,"name":"empty"}

respond(first(List("hello", "world")))
// 200 OK
// hello
```

## 3. Curating and Re-using Common Errors

Since `E` is immutable and has a fluent API, you can keep common errors together and use them as a base for your more customized cases. This way you don't always have to provide all the details of an error. It also helps you organize your error definitions.

Here's one example in Java:

```java
import e.java.E;
import e.java.EOr;
import java.util.Arrays;

public final class Errors {
    public static final E invalidData =
        new E(400, "invalid-data", "Provided data is invalid!", null, null, null);
    
    public static final E notFound =
        new E(404, "not-found", "Requested resource does not exist!", null, null, null);

    private Errors() {}
}

public EOr<Integer> extractNegativeNumber(List<String> list) {
    if (list == null || list.isEmpty()) {
        return Errors.invalidData.message("List is empty").toEOr();
    }
  
    for (String s : list) {
        EOr<Integer> eor = EOr.catching(
            () -> Integer.parseInt(s),
            t  -> Errors.invalidData.message("Not a number").cause(E.fromThrowable(t)).data("value", s)
        );

        if (eor.hasError()) {
            return eor;
        }

        if (eor.filter(i -> i < 0).hasValue()) {
            return eor;
        }
    }
    
    return Errors.notFound.message("Cannot find negative number").toEOr();
}

EOr<Integer> eor1 = extractNegativeNumber(null);
// hasError: true
// hasValue: false
// {"name":"invalid-data","message":"List is empty","code":400}

EOr<Integer> eor2 = extractNegativeNumber(Arrays.asList("a", "b"));
// hasError: true
// hasValue: false
// {"name":"invalid-data","message":"Not a number","code":400,"data":{"value":"a"}}

EOr<Integer> eor3 = extractNegativeNumber(Arrays.asList(1, 2, 3));
// hasError: true
// hasValue: false
// {"name":"invalid-data","message":"Cannot find negative number","code":400}

EOr<Integer> eor4 = extractNegativeNumber(Arrays.asList(1, -1));
// hasError: false
// hasValue: true
// -1
```

## 4. Validating User Input

User input is never to be trusted and must always be validated. The result of an invalid user input is naturally an error, usually an expected one. Representing these as `E`s and using `EOr` can help you deal with this.

Here's a validator in Kotlin:

```kotlin
import e.kotlin.*

data class User(val email: String, val password: String)

object UserValidator {
    fun validate(user: User): EOr<Unit> =
        validateEmail(user.email).andThen {
            validatePassword(user.password).andThen {
                EOr.unit
            }
        }

    private fun validateEmail(email: String): EOr<Unit> {
        val e = email.trim()
        return when {
            e.isEmpty()     -> E.name("email-empty").toEOr()
            e.contains("@") -> E.name("email-invalid").toEOr()
            else            -> EOr.unit
        }
    }

    private fun validatePassword(password: String): EOr<Unit> {
        val p = password.trim()
        return when {
            p.isEmpty()  -> E.name("password-empty").toEOr()
            p.length < 6 -> E.name("password-too-short").toEOr()
            else         -> EOr.unit
        }
    }
}

fun printIfValid(user: User): Unit =
    UserValidator.validate(user).fold(
        { e -> print(e) },
        { u -> print(u) }
    )

printIfValid(User("", ""))                    // {"name":"email-empty"}
printIfValid(User("foo", ""))                 // {"name":"email-invalid"}
printIfValid(User("foo@bar.com", ""))         // {"name":"password-empty"}
printIfValid(User("foo@bar.com", "asd"))      // {"name":"password-too-short"}
printIfValid(User("foo@bar.com", "password")) // User("foo@bar.com", "password")
```
