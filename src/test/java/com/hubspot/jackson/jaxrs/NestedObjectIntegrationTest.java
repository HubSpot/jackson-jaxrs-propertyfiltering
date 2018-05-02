package com.hubspot.jackson.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hubspot.jackson.jaxrs.util.TestResource.TestObject;

public class NestedObjectIntegrationTest extends BaseTest {

  private static final TypeReference<Map<Long, TestObject>> MAP_NESTED_TYPE = new TypeReference<Map<Long, TestObject>>() {};

  @Test
  public void testNestedObject() throws IOException {
    Map<Long, TestObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "*.name");

    assertThat(objects).hasSize(10);
    for (long i = 0; i < 10; i++) {
      assertThat(objects).containsKeys(i);

      assertThat(objects.get(i).getId()).isNull();
      assertThat(objects.get(i).getName()).isEqualTo("Test " + i);
    }
  }

  @Test
  public void testNestedExclusions() throws IOException {
    Map<Long, TestObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "!*.name");

    assertThat(objects).hasSize(10);
    for (long i = 0; i < 10; i++) {
      assertThat(objects).containsKeys(i);

      assertThat(objects.get(i).getId()).isEqualTo(i);
      assertThat(objects.get(i).getName()).isNull();
    }
  }

  @Test
  public void testNestedObjectWithMultiplePropertyLevels() throws IOException {
    Map<Long, TestObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "*.name,9.id");

    assertThat(objects).hasSize(10);
    for (long i = 0; i < 9; i++) {
      assertThat(objects).containsKeys(i);

      assertThat(objects.get(i).getId()).isNull();
      assertThat(objects.get(i).getName()).isEqualTo("Test " + i);
    }

    assertThat(objects).containsKeys(9L);
    assertThat(objects.get(9L).getId()).isEqualTo(9L);
    assertThat(objects.get(9L).getName()).isEqualTo("Test 9");
  }
}