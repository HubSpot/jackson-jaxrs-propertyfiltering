# jackson-jaxrs-propertyfiltering [![Build Status](https://travis-ci.org/HubSpot/jackson-jaxrs-propertyfiltering.svg?branch=master)](https://travis-ci.org/HubSpot/jackson-jaxrs-propertyfiltering)
## Overview

Library to enable automatic filtering of JSON responses from JAX-RS endpoints. Clients specify via query params what properties they want and the library handles filtering the response entities down to just those properties (see [here](#usage) for some examples). Tested with Jersey but should work with any JAX-RS implementation.

## Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.hubspot.jackson</groupId>
  <artifactId>jackson-jaxrs-propertyfiltering</artifactId>
  <version>0.8.4</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

## Configuration

1. Register `PropertyFilteringMessageBodyWriter` with your JAX-RS implementation
2. Annotate the desired endpoints with `@PropertyFiltering`
3. Profit

Yes, it's just that simple.

## Usage

Let's assume you have an endpoint annotated with `@PropertyFiltering` that returns JSON of the form:

```json
{
  "id": 54,
  "name": "Object",
  "child": {
    "id": 96,
    "name": "Child Object"
  }
}
```

If you just need the `id` field you can pass `?property=id` which will return:

```json
{
  "id": 54
}
```

Nested fields are also supported with dot-notation, so if you need the child `id` as well, you can pass `?property=id&property=child.id` which will return:

```json
{
  "id": 54,
  "child": {
    "id": 96
  }
}
```

You can also specify fields by exclusion rather than inclusion, so if you don't need the child field but want everything else you can pass `?property=!child` which will return:

```json
{
  "id": 54,
  "name": "Object"
}
```
