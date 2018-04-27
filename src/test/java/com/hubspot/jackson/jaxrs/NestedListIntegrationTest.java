package com.hubspot.jackson.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hubspot.jackson.jaxrs.util.TestResource.TestNestedObject;

public class NestedListIntegrationTest extends BaseTest {

  private static TypeReference<List<TestNestedObject>> listNestedType = new TypeReference<List<TestNestedObject>>() { };

  @Test
  public void testNestedObject() throws IOException {
    List<TestNestedObject> objects = getObjects(listNestedType, "/nested/list", "property", "id,nested.name");

    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getId()).isEqualTo((long) i * 100);
      assertThat(objects.get(i).getName()).isNull();

      assertThat(objects.get(i).getNested().getName()).isEqualTo("Test " + i);
      assertThat(objects.get(i).getNested().getId()).isNull();
    }
  }

  @Test
  public void testNestedObjectWithExclusion() throws IOException {
    List<TestNestedObject> objects = getObjects(listNestedType, "/nested/list", "property", "!nested.id");

    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getNested().getName()).isEqualTo("Test " + i);
      assertThat(objects.get(i).getNested().getId()).isNull();
    }
  }
}
