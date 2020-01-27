package com.hubspot.jackson.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hubspot.jackson.jaxrs.util.TestResource.TestArrayObject;
import com.hubspot.jackson.jaxrs.util.TestResource.TestNestedObject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ArrayIntegrationTest extends BaseTest {

  private static final TypeReference<List<TestArrayObject>> LIST_ARRAY_TYPE = new TypeReference<List<TestArrayObject>>() {};
  private static final TypeReference<Map<Long, TestArrayObject>> MAP_ARRAY_TYPE = new TypeReference<Map<Long, TestArrayObject>>() {};
  private static final TypeReference<TestArrayObject> ARRAY_OBJECT_TYPE = new TypeReference<TestArrayObject>() {};

  @Test
  public void testArrayList() throws IOException {
    List<TestArrayObject> objects = getObjects(LIST_ARRAY_TYPE, "/array/list", "property", "id,nested.secondNested.name");

    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getId()).isEqualTo((long) i);
      assertThat(objects.get(i).getName()).isNull();

      assertThat(objects.get(i).getNested()).hasSize(10);
      for (int j = 0; j < 10; j++) {
        TestNestedObject nested = objects.get(i).getNested().get(j);

        assertThat(nested.getNested()).isNull();

        assertThat(nested.getSecondNested().getName()).isEqualTo("SecondNested Test " + (i + j) * 1_000);
        assertThat(nested.getSecondNested().getId()).isNull();
      }
    }
  }

  @Test
  public void testArrayListWithExclusion() throws IOException {
    List<TestArrayObject> objects = getObjects(LIST_ARRAY_TYPE, "/array/list", "property", "!nested.secondNested");

    assertThat(objects).hasSize(10);
    for (int i = 0; i < 10; i++) {
      assertThat(objects.get(i).getId()).isEqualTo((long) i);
      assertThat(objects.get(i).getName()).isEqualTo("Test " + i);

      assertThat(objects.get(i).getNested()).hasSize(10);
      for (int j = 0; j < 10; j++) {
        TestNestedObject nested = objects.get(i).getNested().get(j);

        assertThat(nested.getNested().getName()).isEqualTo("Nested Test " + (i + j) * 100);
        assertThat(nested.getNested().getId()).isEqualTo((i + j) * 100);

        assertThat(nested.getSecondNested()).isNull();
      }
    }
  }

  @Test
  public void testArrayObject() throws IOException {
    Map<Long, TestArrayObject> objects = getObjects(MAP_ARRAY_TYPE, "/array/object", "property", "*.name");

    assertThat(objects).hasSize(10);
    for (long i = 0; i < 10; i++) {
      assertThat(objects).containsKeys(i);

      TestArrayObject object = objects.get(i);

      assertThat(object.getId()).isNull();
      assertThat(object.getName()).isEqualTo("Test " + i);

      assertThat(object.getNested()).isNull();
    }
  }

  @Test
  public void testArrayWithoutPrefix() throws IOException {
    // no prefix, should select root properties
    TestArrayObject object = getObjects(ARRAY_OBJECT_TYPE, "/array", "property", "id,name");

    assertThat(object.getNested()).isNull();
    assertThat(object.getId()).isEqualTo(1);
    assertThat(object.getName()).isEqualTo("Test 1");
  }

  @Test
  public void testArrayExclusions() throws IOException {
    Map<Long, TestArrayObject> objects = getObjects(MAP_ARRAY_TYPE, "/array/object", "property", "!*.name");

    assertThat(objects).hasSize(10);
    for (long i = 0; i < 10; i++) {
      assertThat(objects).containsKeys(i);

      TestArrayObject object = objects.get(i);

      assertThat(object.getId()).isEqualTo(i);
      assertThat(object.getName()).isNull();

      assertThat(object.getNested()).hasSize(10);
      for (int j = 0; j < 10; j++) {
        TestNestedObject nested = object.getNested().get(j);

        assertThat(nested.getNested().getName()).isEqualTo("Nested Test " + (i + j) * 100);
        assertThat(nested.getNested().getId()).isEqualTo((i + j) * 100);

        assertThat(nested.getSecondNested().getName()).isEqualTo("SecondNested Test " + (i + j) * 1_000);
        assertThat(nested.getSecondNested().getId()).isEqualTo((i + j) * 1_000);
      }
    }
  }

  @Test
  public void testSecondLevelWildcard() throws IOException {
    Map<Long, TestArrayObject> objects = getObjects(MAP_ARRAY_TYPE, "/array/object", "property", "9.*");

    assertThat(objects).containsOnlyKeys(9L);

    TestArrayObject object = objects.get(9L);

    assertThat(object.getId()).isEqualTo(9L);
    assertThat(object.getName()).isEqualTo("Test 9");

    assertThat(object.getNested()).hasSize(10);
    for (int j = 0; j < 10; j++) {
      TestNestedObject nested = object.getNested().get(j);

      assertThat(nested.getNested().getName()).isEqualTo("Nested Test " + (j + 9) * 100);
      assertThat(nested.getNested().getId()).isEqualTo((j + 9) * 100);

      assertThat(nested.getSecondNested().getName()).isEqualTo("SecondNested Test " + (j + 9) * 1_000);
      assertThat(nested.getSecondNested().getId()).isEqualTo((j + 9) * 1_000);
    }
  }

  @Test
  public void testSecondLevelWildcardExclusion() throws IOException {
    Map<Long, TestArrayObject> objects = getObjects(MAP_ARRAY_TYPE, "/array/object", "property", "!9.*");

    assertThat(objects).containsKeys(9L);
    assertThat(objects.get(9L).getId()).isNull();
    assertThat(objects.get(9L).getName()).isNull();
    assertThat(objects.get(9L).getNested()).isNull();

    for (long i = 0; i < 9; i++) {
      assertThat(objects).containsKeys(i);

      TestArrayObject object = objects.get(i);

      assertThat(object.getId()).isEqualTo(i);
      assertThat(object.getName()).isEqualTo("Test " + i);

      assertThat(object.getNested()).hasSize(10);
      for (int j = 0; j < 10; j++) {
        TestNestedObject nested = object.getNested().get(j);

        assertThat(nested.getNested().getName()).isEqualTo("Nested Test " + (i + j) * 100);
        assertThat(nested.getNested().getId()).isEqualTo((i + j) * 100);

        assertThat(nested.getSecondNested().getName()).isEqualTo("SecondNested Test " + (i + j) * 1_000);
        assertThat(nested.getSecondNested().getId()).isEqualTo((i + j) * 1_000);
      }
    }
  }

  @Test
  public void testMiddleLevelWildcard() throws IOException {
    Map<Long, TestArrayObject> objects = getObjects(MAP_ARRAY_TYPE, "/array/object", "property", "9.*.*.name");

    assertThat(objects).containsOnlyKeys(9L);
    TestArrayObject object = objects.get(9L);

    assertThat(object.getId()).isEqualTo(9L);
    assertThat(object.getName()).isEqualTo("Test 9");

    assertThat(object.getNested()).hasSize(10);
    for (int j = 0; j < 10; j++) {
      TestNestedObject nested = object.getNested().get(j);

      assertThat(nested.getNested().getName()).isEqualTo("Nested Test " + (j + 9) * 100);
      assertThat(nested.getNested().getId()).isNull();

      assertThat(nested.getSecondNested().getName()).isEqualTo("SecondNested Test " + (j + 9) * 1_000);
      assertThat(nested.getSecondNested().getId()).isNull();
    }
  }

  @Test
  public void testMiddleLevelWildcardExclusion() throws IOException {
    Map<Long, TestArrayObject> objects = getObjects(MAP_ARRAY_TYPE, "/array/object", "property", "!9.*.*.name");

    assertThat(objects).containsKeys(9L);
    TestArrayObject object = objects.get(9L);

    assertThat(object.getId()).isEqualTo(9L);
    assertThat(object.getName()).isEqualTo("Test 9");

    assertThat(objects.get(9L).getNested()).hasSize(10);
    for (int j = 0; j < 10; j++) {
      TestNestedObject nested = object.getNested().get(j);

      assertThat(nested.getNested().getName()).isNull();
      assertThat(nested.getNested().getId()).isEqualTo((j + 9) * 100);

      assertThat(nested.getSecondNested().getName()).isNull();
      assertThat(nested.getSecondNested().getId()).isEqualTo((j + 9) * 1_000);
    }

    for (long i = 0; i < 9; i++) {
      assertThat(objects).containsKeys(i);

      object = objects.get(i);

      assertThat(object.getId()).isEqualTo(i);
      assertThat(object.getName()).isEqualTo("Test " + i);

      assertThat(object.getNested()).hasSize(10);
      for (int j = 0; j < 10; j++) {
        TestNestedObject nested = object.getNested().get(j);

        assertThat(nested.getNested().getName()).isEqualTo("Nested Test " + (i + j) * 100);
        assertThat(nested.getNested().getId()).isEqualTo((i + j) * 100);

        assertThat(nested.getSecondNested().getName()).isEqualTo("SecondNested Test " + (i + j) * 1_000);
        assertThat(nested.getSecondNested().getId()).isEqualTo((i + j) * 1_000);
      }
    }
  }

  @Test
  public void testArrayObjectWithMultiplePropertyLevels() throws IOException {
    Map<Long, TestArrayObject> objects = getObjects(MAP_ARRAY_TYPE, "/array/object", "property", "*.name,9.id");

    assertThat(objects).hasSize(10);
    for (long i = 0; i < 9; i++) {
      assertThat(objects).containsKeys(i);

      assertThat(objects.get(i).getId()).isNull();
      assertThat(objects.get(i).getName()).isEqualTo("Test " + i);
      assertThat(objects.get(i).getNested()).isNull();
    }

    assertThat(objects).containsKeys(9L);
    assertThat(objects.get(9L).getId()).isEqualTo(9L);
    assertThat(objects.get(9L).getName()).isEqualTo("Test 9");
    assertThat(objects.get(9L).getNested()).isNull();
  }
}
