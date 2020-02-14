# e-gson

This module contains [`GsonAdapterForE`](src/main/java/e/gson/GsonAdapterForE.java) as a `Codec` implementation of `E` using [gson](https://github.com/google/gson). There is also [`GsonSerializerForMaybe`](src/main/java/e/gson/GsonSerializerForMaybe.java) for serializing as `E` or the value itself depending on whether the `Maybe` is failure or not.

```java
import e.AbstractDecoder.DecoderResult;
import e.java.E;
import e.gson.GsonAdapterForE;
import com.google.gson.*;

GsonAdapterForE adapter = GsonAdapterForE.get();
GsonSerializerForMaybe serializer = GsonSerializerForMaybe.get();
Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(E.class, adapter)
            .registerTypeAdapterFactory(TreeTypeAdapter.newTypeHierarchyFactory(Maybe.class, serializer)
            .create();

E empty = E.empty();

E e = new E("test-name", "Test Message", 1, new Exception("Test Exception")).data("test", "data");

/*****************************/
/* Encoding E as JsonElement */
/*****************************/

JsonElement encoded1 = adapter.encode(empty);
// {}

JsonElement encoded2 = adapter.encode(e);
// {"name":"test-name","message":"Test Message","code":1,"cause":"Test Exception","data":{"test":"data"}}

/*****************************/
/* Decoding JsonElement as E */
/*****************************/

JsonArray arr1 = new JsonArray();
arr1.add(1);
arr1.add(2);

DecodingResult<E> result1 = adapter.decode(arr1);

boolean isSuccess1 = result1.isSuccess;
// false

E decoded1 = result1.get();
// {"name":"decoding-failure","message":"Cannot decode as E!","cause":"Not a JSON Object: [1,2]","data":{"input":"[1,2]"}}

JsonObject json1 = new JsonObject();
json1.add("name", new JsonPrimitive("test-name"));
json1.add("message", new JsonPrimitive("Test Message"));
json1.add("code", new JsonPrimitive(1));
JsonObject data1 = new JsonObject();
data.add("test", new JsonPrimitive("data"));
json1.add("data", data1);

DecodingResult<E> result2 = adapter.decode(json1);

boolean isSuccess2 = result2.isSuccess;
// true

E decoded2 = result2.get();
// {"name":"test-name","message":"Test Message","code":1,"cause":"Test Exception","data":{"test":"data"}}

/*****************/
/* Serializing E */
/*****************/

JsonElement serialized1 = gson.toJsonTree(empty);
String toJson1 = gson.toJson(empty);
// {}

JsonElement serialized2 = gson.toJsonTree(e);
String toJson2 = gson.toJson(e);
// {"name":"test-name","message":"Test Message","code":1,"cause":"Test Exception","data":{"test":"data"}}

/*******************/
/* Deserializing E */
/*******************/

JsonArray arr2 = new JsonArray();
arr2.add(1);
arr2.add(2);

E deserialized1 = gson.fromJson(arr2, E.class);
// Throws JsonParseException: "Not a JSON Object: [1,2]"

JsonObject json2 = new JsonObject();
json2.add("name", new JsonPrimitive("test-name"));
json2.add("message", new JsonPrimitive("Test Message"));
json2.add("code", new JsonPrimitive(1));
JsonObject data2 = new JsonObject();
data2.add("test", new JsonPrimitive("data"));
json2.add("data", data2);

E deserialized2 = gson.fromJson(json2, E.class);
// {"name":"test-name","message":"Test Message","code":1,"cause":"Test Exception","data":{"test":"data"}}

/*********************/
/* Serializing Maybe */
/*********************/

Maybe<String> maybe1 = Maybe.failure(e);
JsonElement serialized3 = gson.toJsonTree(maybe1);
String toJson3 = gson.toJson(maybe1);
// {"name":"test-name","message":"Test Message","code":1,"cause":"Test Exception","data":{"test":"data"}}

Map<String, String> map = new HashMap<>();
map.put("foo", "bar");
Maybe<Map<String, String>> maybe2 = Maybe.success(map);
JsonElement serialized4 = gson.toJsonTree(maybe2);
String toJson4 = gson.toJson(maybe2);
// {"foo":"bar"}
```
