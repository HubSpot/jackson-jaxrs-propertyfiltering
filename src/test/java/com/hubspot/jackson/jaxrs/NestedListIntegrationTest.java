package com.hubspot.jackson.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hubspot.jackson.jaxrs.util.TestResource.TestNestedObject;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class NestedListIntegrationTest extends BaseTest {

  private static final TypeReference<List<TestNestedObject>> LIST_NESTED_TYPE =
    new TypeReference<List<TestNestedObject>>() {};

  @Test
  public void testNestedObject() throws IOException {
    List<TestNestedObject> objects = getObjects(
      LIST_NESTED_TYPE,
      "/nested/list",
      "property",
      "id,nested.name"
    );

    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getId()).isEqualTo((long) i);
      assertThat(objects.get(i).getName()).isNull();

      assertThat(objects.get(i).getNested().getName())
        .isEqualTo("Nested Test " + i * 100);
      assertThat(objects.get(i).getNested().getId()).isNull();
    }
  }

  @Test
  public void testNestedObjectWithExclusion() throws IOException {
    List<TestNestedObject> objects = getObjects(
      LIST_NESTED_TYPE,
      "/nested/list",
      "property",
      "!nested.id"
    );

    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getNested().getName())
        .isEqualTo("Nested Test " + i * 100);
      assertThat(objects.get(i).getNested().getId()).isNull();
    }
  }
}
