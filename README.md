## Overview

Library to enable automatic filtering of JSON responses from JAX-RS endpoints. Clients specify via query params what properties they want and the library handles filtering the response entities down to just those properties. Tested with Jersey but should work with any JAX-RS implementation.

## Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.hubspot.jackson</groupId>
  <artifactId>jackson-jaxrs-propertyfiltering</artifactId>
  <version>0.1.0</version>
</dependency>
```

(or whatever version is most up-to-date at the moment)

## Configuration

1. Register `PropertyFilteringMessageBodyWriter` with your JAX-RS implementation
2. Annotate the desired endpoints with `@PropertyFiltering`
3. Profit

Yes, it's just that simple.
