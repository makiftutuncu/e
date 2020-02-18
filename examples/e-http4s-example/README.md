# e http4s Example

This is a demo project written in Java with [http4s](https://http4s.org/). It is a CRUD backend for people with very basic functionality. It uses

* [H2](https://www.h2database.com) for database
* [doobie](https://tpolecat.github.io/doobie) for database access
* [circe](https://circe.github.io/circe) for Json serialization/deserialization

## API

### 1. Creating a Person

Creates a new person with given data

#### Example Request

All fields are required.

```
POST /people

{
    "name": "Akif",
    "age": 28
}
```

#### Example Response

A successful response will have `201 Created` status.

```
201 Created

{
    "id": 1,
    "name": "Akif",
    "age": 28
}
```

### 2. Listing People

Lists all people.

#### Example Request

```
GET /people
```

#### Example Response

Response payload will contain a Json array of all people.

```
200 OK

[
  {
    "id": 1,
    "name": "Akif",
    "age": 28
  }
]
```

### 3. Getting a Person

Gets a person with given id

#### Example Request

```
GET /people/{id}
```

#### Example Response

Response payload will contain a Json object of the person.

```
200 OK

{
    "id": 1,
    "name": "Akif",
    "age": 28
}
```

### 4. Updating a Person

Updates a person with given data

#### Example Request

All fields are optional.

```
PUT /people/{id}

{
    "name": "Mehmet Akif",
    "age": 29
}
```

#### Example Response

Response payload will contain a Json object of the person.

```
200 OK

{
    "id": 1,
    "name": "Mehmet Akif",
    "age": 29
}
```

### 5. Deleting a Person

Deletes given person.

#### Example Request

```
DELETE /people/{id}
```

#### Example Response

Response payload will contain a Json object of the person.

```
200 OK

{
    "id": 1,
    "name": "Mehmet Akif",
    "age": 29
}
```

