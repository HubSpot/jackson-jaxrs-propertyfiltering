package com.hubspot.jackson.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hubspot.jackson.jaxrs.util.TestResource.TestObject;

public class NestedObjectIntegrationTest extends BaseTest {

  private static TypeReference<Map<Long, TestObject>> mapNestedType = new TypeReference<Map<Long, TestObject>>() { };

  @Test
  public void testNestedObject() throws IOException {
    Map<Long, TestObject> objects = getObjects(mapNestedType, "/nested/object", "property", "*.name");

    assertThat(objects).hasSize(10);
    for (long i = 0; i < 10; i++) {
      assertThat(objects).containsKeys(i);

      assertThat(objects.get(i).getId()).isNull();
      assertThat(objects.get(i).getName()).isEqualTo("Test " + i);
    }
  }
}
