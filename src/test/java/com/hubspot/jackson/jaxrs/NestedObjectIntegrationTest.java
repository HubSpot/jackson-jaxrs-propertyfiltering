package com.hubspot.jackson.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hubspot.jackson.jaxrs.util.TestResource.TestNestedObject;

public class NestedObjectIntegrationTest extends BaseTest {

  private static final TypeReference<Map<Long, TestNestedObject>> MAP_NESTED_TYPE = new TypeReference<Map<Long, TestNestedObject>>() {};
  private static final TypeReference<TestNestedObject> NESTED_OBJECT_TYPE = new TypeReference<TestNestedObject>() {};

  @Test
  public void testNestedObject() throws IOException {
    Map<Long, TestNestedObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "*.name");

    assertThat(objects).hasSize(10);
    for (long i = 0; i < 10; i++) {
      assertThat(objects).containsKeys(i);

      assertThat(objects.get(i).getId()).isNull();
      assertThat(objects.get(i).getName()).isEqualTo("Test " + i);
    }
  }

  @Test
  public void testNestedWithoutPrefix() throws IOException {
    // no prefix, should select root properties
    TestNestedObject object = getObjects(NESTED_OBJECT_TYPE, "/nested", "property", "id,name");

    assertThat(object.getNested()).isNull();
    assertThat(object.getSecondNested()).isNull();
    assertThat(object.getId()).isEqualTo(1);
    assertThat(object.getName()).isEqualTo("Test 1");
  }

  @Test
  public void testNestedPrefixWithoutPeriod() throws IOException {
    // with prefix, should select nested object properties
    TestNestedObject object = getObjects(NESTED_OBJECT_TYPE, "/prefix", "property", "id,name");

    assertThat(object.getId()).isNull();
    assertThat(object.getName()).isNull();
    assertThat(object.getSecondNested()).isNull();
    assertThat(object.getNested().getId()).isEqualTo(100);
    assertThat(object.getNested().getName()).isEqualTo("Nested Test 100");
  }

  @Test
  public void testNestedPrefixWithPeriod() throws IOException {
    // with prefix, should select nested object properties
    TestNestedObject object = getObjects(NESTED_OBJECT_TYPE, "/prefix/period", "property", "id,name");

    assertThat(object.getId()).isNull();
    assertThat(object.getName()).isNull();
    assertThat(object.getSecondNested()).isNull();
    assertThat(object.getNested().getId()).isEqualTo(100);
    assertThat(object.getNested().getName()).isEqualTo("Nested Test 100");
  }

  @Test
  public void testNestedExclusions() throws IOException {
    Map<Long, TestNestedObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "!*.name");

    assertThat(objects).hasSize(10);
    for (long i = 0; i < 10; i++) {
      assertThat(objects).containsKeys(i);

      assertThat(objects.get(i).getId()).isEqualTo(i);
      assertThat(objects.get(i).getName()).isNull();
    }
  }

  @Test
  public void testSecondLevelWildcard() throws IOException {
    Map<Long, TestNestedObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "9.*");

    assertThat(objects).containsOnlyKeys(9L);
    assertThat(objects.get(9L).getId()).isEqualTo(9L);
    assertThat(objects.get(9L).getName()).isEqualTo("Test 9");
  }

  @Test
  public void testSecondLevelWildcardExclusion() throws IOException {
    Map<Long, TestNestedObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "!9.*");

    assertThat(objects).containsKeys(9L);
    assertThat(objects.get(9L).getId()).isNull();
    assertThat(objects.get(9L).getName()).isNull();

    for (long i = 0; i < 9; i++) {
      assertThat(objects).containsKeys(i);

      assertThat(objects.get(i).getId()).isEqualTo(i);
      assertThat(objects.get(i).getName()).isEqualTo("Test " + i);
    }
  }

  @Test
  public void testMiddleLevelWildcard() throws IOException {
    Map<Long, TestNestedObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "9.*.name");

    assertThat(objects).containsOnlyKeys(9L);
    TestNestedObject testNestedObject = objects.get(9L);

    assertThat(testNestedObject.getId()).isEqualTo(9L);
    assertThat(testNestedObject.getName()).isEqualTo("Test 9");

    assertThat(testNestedObject.getNested()).isNotNull();
    assertThat(testNestedObject.getNested().getId()).isNull();
    assertThat(testNestedObject.getNested().getName()).isEqualTo("Nested Test 900");

    assertThat(testNestedObject.getSecondNested()).isNotNull();
    assertThat(testNestedObject.getSecondNested().getId()).isNull();
    assertThat(testNestedObject.getSecondNested().getName()).isEqualTo("SecondNested Test 9000");
  }

  @Test
  public void testMiddleLevelWildcardExclusion() throws IOException {
    Map<Long, TestNestedObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "!9.*.name");

    assertThat(objects).containsKeys(9L);
    TestNestedObject testNestedObject = objects.get(9L);

    assertThat(testNestedObject.getId()).isEqualTo(9L);
    assertThat(testNestedObject.getName()).isEqualTo("Test 9");

    assertThat(testNestedObject.getNested()).isNotNull();
    assertThat(testNestedObject.getNested().getId()).isEqualTo(900L);
    assertThat(testNestedObject.getNested().getName()).isNull();

    assertThat(testNestedObject.getSecondNested()).isNotNull();
    assertThat(testNestedObject.getSecondNested().getId()).isEqualTo(9000L);
    assertThat(testNestedObject.getSecondNested().getName()).isNull();

    for (long i = 0; i < 9; i++) {
      assertThat(objects).containsKeys(i);

      assertThat(objects.get(i).getId()).isEqualTo(i);
      assertThat(objects.get(i).getName()).isEqualTo("Test " + i);

      assertThat(objects.get(i).getNested()).isNotNull();
      assertThat(objects.get(i).getNested().getId()).isEqualTo(i * 100);
      assertThat(objects.get(i).getNested().getName()).isEqualTo("Nested Test " + i * 100);

      assertThat(objects.get(i).getSecondNested()).isNotNull();
      assertThat(objects.get(i).getSecondNested().getId()).isEqualTo(i * 1_000);
      assertThat(objects.get(i).getSecondNested().getName()).isEqualTo("SecondNested Test " + i * 1_000);
    }
  }

  @Test
  public void testNestedObjectWithMultiplePropertyLevels() throws IOException {
    Map<Long, TestNestedObject> objects = getObjects(MAP_NESTED_TYPE, "/nested/object", "property", "*.name,9.id");

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
