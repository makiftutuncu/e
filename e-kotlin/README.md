# e-kotlin

This module contains implementation of `e-core` written in Kotlin.

## E

[`E`](src/main/kotlin/e/kotlin/E.kt) provides a concrete implementation of `AbstractE` as an immutable data class. Any modifications on an `E` instance return a new instance.

Here are some examples:

```kotlin
import e.kotlin.*

/***********************/
/* Empty E definitions */
/***********************/

E.empty
// {}

E()
// {}

/**********************************************************************************/
/* Constructing an E, mix and match constructor and builder methods as you see fit */
/**********************************************************************************/

val ae = java.lang.ArithmeticException()

E(name = "arithmetic-error", message = "Cannot divide number by 0!", code = 1, cause = ae, data = mapOf("input" to "5"))
// {"name":"arithmetic-error","message":"Cannot divide number by 0!","code:1,"cause":"...","data":{"input":"5"}}

E.empty.name("arithmetic-error").message("Cannot divide number by 0!").code(1).cause(ae).data("input" to "5")
// {"name":"arithmetic-error","message":"Cannot divide number by 0!","code:1,"cause":"...","data":{"input":"5"}}

E("arithmetic-error", "Cannot divide number by 0!").cause(ae).data("input", "5")
// {"name":"arithmetic-error","message":"Cannot divide number by 0!","code:1,"cause":"...","data":{"input":"5"}}

/****************************************/
/* Checking existence of fields of an E */
/****************************************/

val errorContainsCode = E("error").hasCode()
// false

/****************************/
/* Accessing fields of an E */
/****************************/

val errorContainsInput = E(data = mapOf("input" to "test")).data()["input"] != null
// true

/*************************************************************/
/* Converting E to an Exception, for throwing stuff (unsafe) */
/*************************************************************/

E(message = "test").toException()

/*******************************************************/
/* Converting E to a Maybe, for returning stuff (safe) */
/*******************************************************/

E("error").toMaybe<Int>()
// {"name":"error"}

```

## Maybe

[`Maybe`](src/main/kotlin/e/kotlin/Maybe.kt) provides a type safe way to denote a value. It can either contain an `E` or a value of type `A`.

Here are some examples:

```kotlin
import e.kotlin.*

/********************************/
/* Constructing a success Maybe */
/********************************/

val maybe1: Maybe<Int> = Maybe.success(5)
// 5

val maybe2 = true.toMaybe()
// true

/********************************/
/* Constructing a failure Maybe */
/********************************/

val maybe3: Maybe<Int> = Maybe.failure(E("e-1"))
// {"name":"e-1"}

val maybe4 = E("e-2").toMaybe<Boolean>()
// {"name":"e-2"}

/********************************/
/* Constructing a unit Maybe */
/********************************/

Maybe.unit()
// kotlin.Unit

val maybe4 = E("e-2").toMaybe<Boolean>()
// {"name":"e-2"}

/*******************************/
/* Checking content of a Maybe */
/*******************************/

5.toMaybe().isSuccess()
// true

E("error").toMaybe<Int>().isSuccess()
// false

5.toMaybe().e
// null

E("error").toMaybe<Int>().e
// {"name":"error"}

5.toMaybe().value
// 5

E("error").toMaybe<Int>().value
// null

/*******************/
/* Mapping a Maybe */
/*******************/

E("error").toMaybe<Int>().map(i => i * 2)
// {"name":"error"}

5.toMaybe().map(i => i * 2)
// 10

/************************/
/* Flat mapping a Maybe */
/************************/

E("error").toMaybe<Int>().flatMap(i => (i * 2).toMaybe())
// {"name":"error"}

5.toMaybe().flatMap(i => E("error").toMaybe<Boolean>())
// {"name":"error"}

5.toMaybe().flatMap(i => (i * 2).toString().toMaybe())
// 10

/*******************/
/* Folding a Maybe */
/*******************/

E("error").toMaybe<Int>().fold({e -> "failure"}, {value -> "success"})
// failure

5.toMaybe().fold({e -> "failure"}, {value -> "success"})
// success

/**********************************************************/
/* Getting value of a Maybe and providing a default value */
/**********************************************************/

E("error").toMaybe<Int>().getOrElse { 0 }
// 0

"test".toMaybe().getOrElse { "" }
// "test"

/***************************************/
/* Providing an alternative to a Maybe */
/***************************************/

E("error1").toMaybe<Int>().orElse { E("error2").toMaybe<Int>() }
// {"name":"error2"}

E("error1").toMaybe<Int>().orElse { "default".toMaybe() }
// "default"

"test-1".toMaybe().orElse { E("error").toMaybe<String>() }
// "test-1"

"test-1".toMaybe().orElse { "test-2".toMaybe() }
// "test-1"

/*******************************************************/
/* Ignoring previous value and moving to another Maybe */
/*******************************************************/

E("error").toMaybe<Int>().andThen { E("error2").toMaybe<Int>() }
// {"name":"error"}

E("error1").toMaybe<Int>().andThen { "default".toMaybe() }
// {"name":"error1"}

"test".toMaybe().andThen { E("error").toMaybe<String>() }
// {"name":"error"}

"test-1".toMaybe().andThen { "test-2".toMaybe() }
// test-2

/*********************/
/* Filtering a Maybe */
/*********************/

E("error").toMaybe<Int>().filter { it < 4 }
// {"name":"error"}

E("error").toMaybe<Int>().filter({ it < 4 }, { E("error-2").data("value" to it) })
// {"name":"error"}

5.toMaybe().filter { it < 4 }
// {"name":"predicate-failed","message":"Value did not satisfy predicate!","data":{"value":"5"}}

5.toMaybe().filter({ it < 4 }, { E("error-2").data("value" to it) })
// {"name":"error-2","data":{"value":"5"}}

5.toMaybe().filter { it > 4 }
// 5

5.toMaybe().filter({ it > 4 }, { E("error-2").data("value" to it) })
// 5

/****************************************/
/* Constructing a Maybe from a nullable */
/****************************************/

Maybe.fromNullable<Int>(null, E("error"))
// {"name":"error"}

Maybe.fromNullable(3, E("error"))
// 3

val n: Int? = null
n.toMaybe(E("error"))
// {"name":"error"}

3.toMaybe(E("error"))
// 3

/*****************************************************/
/* Constructing a Maybe from a lambda that can throw */
/*****************************************************/

Maybe.catching<Boolean>({throw Exception("test")}, t => E(cause = t))
// {"cause":"test"}

Maybe.catching<Int>({5}, t => E(cause = t))
// 5

/***********************************************************/
/* Constructing a Maybe from a Maybe lambda that can throw */
/***********************************************************/

Maybe.catchingMaybe<Boolean>({throw Exception("test")}, t => E(cause = t))
// {"cause":"test"}

Maybe.catchingMaybe<Int>({E().toMaybe<Int>()}, t => E(cause = t))
// {}

Maybe.catchingMaybe<Int>({5.toMaybe()}, t => E(cause = t))
// 5
```

## Encoder

[`Encoder<OUT>`](src/main/kotlin/e/kotlin/Codec.kt) provides encode functionality such that given `E` can be converted to another value of type `OUT`. There is [`JsonStringEncoder`](src/main/kotlin/e/kotlin/JsonStringEncoder.kt) as a default `Encoder<String>` implementation.

```kotlin
import e.kotlin.*

/*******************************/
/* Default Json string encoder */
/*******************************/

val encoder: Encoder<String> = JsonStringEncoder

encoder.encode(E())
// {}

encoder.encode(E("test-name", "Test Message", 3, new Exception("Test Cause"), mapOf("test" to "data")))
// {"name":"test-name","message":"Test Message","code:3,"cause":"Test Cause","data":{"test":"data"}}

/******************************************************/
/* Custom CSV-like encoder for demonstration purposes */
/******************************************************/

val csv: Encoder<String> = object : Encoder<String> {
    override fun encode(e: E): String =
        """
           "name","message","code"
           "${e.name()}","${e.message()}","${e.code()}"
        """.trimIndent()
}

csv.encode(E())
// "name","message","code"
// "","","0"

csv.encode(E("test-name", "Test Message", 3, new Exception("Test Cause"), mapOf("test" to "data")))
// "name","message","code"
// "test-name","Test Message","3"
```

## Decoder

[`Decoder<IN>`](src/main/kotlin/e/kotlin/Codec.kt) provides decode functionality such that given an input of type `IN`, an `E` can be constructed. Since decoding often includes parsing, it can possibly fail. `DecoderResult` exists for this purpose. It can either contain successfully decoded `E` or an `E` describing the error occurred during decoding.

```kotlin
import e.kotlin.*
import e.AbstractDecoder.DecodingResult

/******************************************************/
/* Custom CSV-like decoder for demonstration purposes */
/******************************************************/

val csv: Decoder<String> = object : Decoder<String> {
    override fun decode(input: String): DecodingResult<E> =
        when (val row = input.lines().drop(1).firstOrNull()) {
            null -> DecodingResult.fail(E("decoding-failure", "Input did not have 2 rows!"))
            else -> {
                val columns = row.split(",")

                if (columns.size < 3) {
                    DecodingResult.fail(E("decoding-failure", "Input did not have 3 columns!"))
                } else {
                    val name    = unescape(columns[0])
                    val message = unescape(columns[1])

                    (unescape(columns[2]).toInt()).runCatching { this }.fold(
                        { code -> DecodingResult.succeed(E(name, message, code)) },
                        { c    -> DecodingResult.fail(E("decoding-failure", "Invalid code!").cause(c).data("code" to columns[2])) }
                    )
                }
            }
        }

    private fun unescape(s: String): String =
        if (s.startsWith("\"") && s.endsWith("\"")) s.drop(1).dropLast(1) else s
}

val result1 = csv.decode("foo")

result1.isSuccess()
// false

result1.get()
// {"name":"decoding-failure","message":"Input did not have 2 rows!"}

val result2 = csv.decode(
  """
     "name","message","code"
     "test-name","Test Message","1"
  """.trimIndent()
)

result2.isSuccess()
// true

result2.get()
// {"name":"test-name","message":"Test Message","code":1}

val maybe1 = csv.decodeMaybe("foo")
// {"name":"decoding-failure","message":"Input did not have 2 rows!"}

maybe1.isSuccess()
// false

val maybe2 = csv.decodeMaybe(
  """
     "name","message","code"
     "test-name","Test Message","1"
  """.trimIndent()
)
// {"name":"test-name","message":"Test Message","code":1}

maybe2.isSuccess()
// true

```

## Codec

[`Codec<A>`](src/main/kotlin/e/kotlin/Codec.kt) is simply a combination of both `Encoder<A>` and `Decoder<A>` for the same type `A`.
