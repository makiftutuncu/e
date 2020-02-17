# Example Projects and Use Cases for e

You can find some example projects featuring e under this directory.

| Project                             | Link                     |
| ----------------------------------- | ------------------------ |
| Spring Boot project written in Java | [Link](e-spring-example) |

Here are some general uses cases of e that are implemented in these example projects.

## 1. Eliminating the Need for Exceptions

Exceptions are dangerous and costly. Using them as a means of representing errors is not ideal. To avoid it, one could treat errors as values like any other data in the code. Then we can pass errors around but this is not always trivial.

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
      return new E("invalid-argument", "List has more than 1 items").data(
        "size", list.size()
      ).toMaybe();
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

You can see that using e, we can eliminate the need for exceptions and write safer, more expressive code.

## 2. Using `code` of an `E` as HTTP Status Code

HTTP defines many status codes (for example https://tools.ietf.org/html/rfc2324#section-2.3.2). When serving HTTP requests from a backend application, we use different codes for different cases.

`E` type contains an integer `code` field that can be useful for mapping an error to an HTTP status code. You can create an error with a code and pass it around. When you reach the edge of your application where you will build an HTTP response, you can use the `code` field to decide which HTTP status to use.

TODO: Example code

## 3. Curating and Re-using Common Errors

Since `E` is immutable and has a fluent API, you can keep common errors together and use them as a base for your more customized cases. This way you don't always have to provide all the details of an error. It also helps you organize your error definitions.

TODO: Example code

## 4. Validating User Input

User input is never to be trusted and must always be validated. The result of an invalid user input is naturally an error, usually an expected one. Representing these as `E`s and using `Maybe` can help you deal with this.

TODO: Example code
