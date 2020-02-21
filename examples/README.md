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

e provides `E` type for treating errors as data and `Maybe` type for wrapping other values that can potentially fail. They both have friendly APIs and by using them, you can leave the exceptions to actual exceptional cases.

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
import e.java.Maybe;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public <A> Maybe<A> one(List<A> list) {
    if (list == null) {
        return new E("invalid-argument", "List is null").toMaybe();
    }
  
    if (list.isEmpty()) {
        return new E("invalid-argument", "List is empty").toMaybe();
    }
  
    if (list.size() != 1) {
        return new E("invalid-argument", "List has more than 1 items")
            .data("size", list.size())
            .toMaybe();
    }
    
    return Maybe.successful(list.get(0));
}

Maybe<String> s = one<String>(null);
// isSuccess: false
// {"name":"invalid-argument","message":"List is null"}

Maybe<Integer> i = one<Integer>(new ArrayList<>());
// isSuccess: false
// {"name":"invalid-argument","message":"List is empty"}

Maybe<Long> l = one<Long>(Arrays.asList(1L, 2L));
// isSuccess: false
// {"name":"invalid-argument","message":"List has more than 1 items","data":{"size":"2"}}

Maybe<String> one = one<String>(Arrays.asList("hello"));
// isSuccess: true
// "hello"
```

You can see that using e, we can eliminate the need for exceptions and write safer, more expressive code. All this happen in compile time, so no runtime surprises either.

## 2. Using `code` of an `E` as HTTP Status Code

HTTP defines many status codes (for example https://tools.ietf.org/html/rfc2324#section-2.3.2). When serving HTTP requests from a backend application, we use different codes for different cases.

`E` type contains an integer `code` field that can be useful for mapping an error to an HTTP status code. You can create an error with a code and pass it around. When you reach the edge of your application where you will build an HTTP response, you can use the `code` field to decide which HTTP status to use.

Here's an example in Play Framework:

```scala
import e.scala.E
import e.scala.Maybe
import play.api.mvc.Results.Status
import play.api.mvc.{Result, Results}
import scala.concurrent.Future

def respond(maybe: Maybe[String], codeIfSuccess: Status = Results.Ok): Future[Result] =
  maybe.fold(
    e     => Future.successful(Status(e.code)(e.toString)),
    value => Future.successful(codeIfSuccess(value))
  )

def first(list: List[String]): Maybe[String] =
  Maybe.fromOption(
    list.headOption,
    E("empty").code(400)
  )

respond(first(List.empty))
// 400 Bad Request
// empty

respond(first(List("hello", "world")))
// 200 OK
// hello
```

## 3. Curating and Re-using Common Errors

Since `E` is immutable and has a fluent API, you can keep common errors together and use them as a base for your more customized cases. This way you don't always have to provide all the details of an error. It also helps you organize your error definitions.

Here's one example in Java:

```java
import e.java.E;
import e.java.Maybe;
import java.util.Arrays;

public final class Errors {
    public static final E invalidData =
        new E("invalid-data", "Provided data is invalid!", 400);
    
    public static final E notFound =
        new E("not-found", "Requested resource does not exist!", 404);

    private Errors() {}
}

public Maybe<Integer> extractNegativeNumber(List<String> list) {
    if (list == null || list.isEmpty()) {
        return Errors.invalidData.message("List is empty").toMaybe();
    }
  
    for (String s : list) {
        Maybe<Integer> maybe = Maybe.catching(
            () -> Integer.parseInt(s),
            t  -> Errors.invalidData.message("Not a number").cause(t).data("value", s)
        );

        if (!maybe.isSuccess()) {
            return maybe;
        }

        if (maybe.filter(i -> i < 0).isSuccess()) {
            return maybe;
        }
    }
    
    return Errors.notFound.message("Cannot find negative number").toMaybe();
}

Maybe<Integer> maybe1 = extractNegativeNumber(null);
// isSuccess: false
// {"name":"invalid-data","message":"List is empty","code":400}

Maybe<Integer> maybe2 = extractNegativeNumber(Arrays.asList("a", "b"));
// isSuccess: false
// {"name":"invalid-data","message":"Not a number","code":400,"data":{"value":"a"}}

Maybe<Integer> maybe3 = extractNegativeNumber(Arrays.asList(1, 2, 3));
// isSuccess: false
// {"name":"invalid-data","message":"Cannot find negative number","code":400}

Maybe<Integer> maybe4 = extractNegativeNumber(Arrays.asList(1, -1));
// isSuccess: true
// -1
```

## 4. Validating User Input

User input is never to be trusted and must always be validated. The result of an invalid user input is naturally an error, usually an expected one. Representing these as `E`s and using `Maybe` can help you deal with this.

Here's a validator in Kotlin:

```kotlin
import e.kotlin.E
import e.kotlin.Maybe

data class User(val email: String, val password: String)

object UserValidator {
    fun validate(user: User): Maybe<Unit> =
        validateEmail(user.email).andThen {
        validatePassword(user.password).andThen {
        Maybe.unit() }}

    private fun validateEmail(email: String): Maybe<Unit> {
        val e = email.trim()
        return when {
            e.isEmpty()     -> E("email-empty").toMaybe()
            e.contains("@") -> E("email-invalid").toMaybe()
            else            -> Maybe.unit()
        }
    }

    private fun validatePassword(password: String): Maybe<Unit> {
        val p = password.trim()
        return when {
            p.isEmpty()  -> E("password-empty").toMaybe()
            p.length < 6 -> E("password-too-short").toMaybe()
            else         -> Maybe.unit()
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
